package com.intersystems.globals.hospmon.doors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.intersystems.globals.hospmon.base.Asset;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.equipment.EquipmentMovement;
import com.intersystems.globals.hospmon.people.Person;
import com.intersystems.globals.hospmon.people.PersonMovement;
import com.intersystems.globals.hospmon.persistence.Context;

public class Door extends Asset<DoorReading, DoorType> {
	private static final Logger logger = LoggerFactory.getLogger(Door.class);
	
	/** Static holds all doors */
	private static List<Door> doors;
	
	/** A figure that indicates the chance this door will be used */
	private int usageChance = 1;
	/** The total number of assets passing through the door in current period */
	private int totalAssetsPeriod;
	
	public Door(DoorType door, String id) {
		super(door, id);
		loadAsset();
	}
	
	/**
	 * Constructs a new instance an optionally saves it.
	 * 
	 * @param door
	 * @param id
	 * @param save
	 */
	public Door(DoorType door, String id, int usageChance, boolean save) {
		this(door, id);
		
		this.usageChance = usageChance;
		
		if( save ) saveAsset();
	}
	
	/**
	 * @return The chance of this door being used
	 */
	public int getUsageChance() {
		return usageChance;
	}
	
	/**
	 * @return the totalAssetsPeriod
	 */
	public int getTotalAssetsPeriod() {
		return totalAssetsPeriod;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Door [");
		buf.append("type=").append(getAssetType().getType());
		buf.append(", id=").append(getId());
		buf.append(", assets=").append(getTotalAssetsPeriod());
		buf.append(", nosReadings=").append(getNosReadings());
		buf.append(", lastSave=").append(getLastSave());
		buf.append("]");
		return buf.toString();
	}

	///////////////////////////////////////////////////
	// Persistence Overrides
	
	@Override
	protected void loadDerived(NodeReference node) {
		// String type = node.exists("type") ? node.getString("type") : null;
		usageChance = node.exists("chance") ? node.getInt("chance") : 1;
	}
	
	@Override
	protected void saveDerived(NodeReference node) {
		node.set(usageChance, "chance");
	}
	
	@Override
	protected void updateDerived(NodeReference node) {
		Context.updateAverage(node, totalAssetsPeriod, node.exists("periods") ? node.getInt("periods") : 0, "averageAssets");
		Context.updateTotal(node, totalAssetsPeriod, "totalAssets");
		totalAssetsPeriod = 0;
	}
	
	@Override
	protected void addDerivedStatNames(List<String> stats) {
		stats.add("AverageAssets");
		stats.add("TotalAssets");
	}
	
	@Override
	protected void getStatisticsDerived(NodeReference node, Map<String, String> stats) {
		stats.put("AverageAssets", node.exists("averageAssets") ? Integer.toString(node.getInt("averageAssets")) : "0");
		stats.put("TotalAssets", node.exists("totalAssets") ? Integer.toString(node.getInt("totalAssets")) : "0");
	}
	
	@Override
	protected void saveReadingDerived(NodeReference node) {
		ValueList lst = Context.getContext().getValueList();
		for( String p : currentReading.getPeople() ) lst.append(p);
		node.set(lst, "people");
		
		lst = Context.getContext().getValueList();
		for( String e : currentReading.getEquipment() ) lst.append(e);
		node.set(lst, "equipment");
		
		totalAssetsPeriod += currentReading.getPeople().size() + currentReading.getEquipment().size();
	}
	
	@Override
	protected DoorReading loadReadingDerived(NodeReference node, Date time) {
		DoorReading reading = new DoorReading(getAssetType(), getId(), time);
		
		ValueList lst = node.getList("people");
		Object[] assets = lst != null ? lst.getAll() : null;
		if( assets != null ) {
			for( Object asset : assets ) {
				reading.addPerson(asset.toString());
			}
		}
		
		lst = node.getList("equipment");
		assets = lst != null ? lst.getAll() : null;
		if( assets != null ) {
			for( Object asset : assets ) {
				reading.addEquipment(asset.toString());
			}
		}
		
		return reading;
	}
	
	//////////////////////////////////////////////////
	// Methods for creating readings
	
	public DoorReading getCurrentReading() {
		if( currentReading == null ) {
			currentReading = new DoorReading(getAssetType(), getId(), new Date());
		}
		
		return currentReading;
	}
	
	public void addToReading(Person[] people, Equipment[] equipment) {
		synchronized(this) {
			DoorReading reading = getCurrentReading();
			
			if( people != null ) {
				for( Person p : people ) {
					if( p == null ) continue;
					
					p.addReading(getId(), currentReading.getTime());
					PersonMovement movement = p.getCurrentReading();
					
					for( Equipment e : equipment ) {
						movement.addEquipment(e.getId());
					}
					
					reading.addPerson(p.getId());
				}
			}
			
			if( equipment != null ) {
				for( Equipment e : equipment ) {
					if( e == null ) continue;
					e.addReading(getId(), currentReading.getTime());
					EquipmentMovement movement = e.getCurrentReading();
					
					for( Person p : people ) {
						movement.addPerson(p.getId());
					}
					
					reading.addEquipment(e.getId());
				}
			}
		}
	}
	
	//////////////////////////////////////////////////
	// Static Methods to get at assets
	
	/**
	 * @return Returns all doors
	 */
	public static List<Door> getDoors() {
		return doors;
	}
	
	/**
	 * @return The door requested
	 */
	public static Door getDoor(String id) {
		for( Door d : doors ) {
			if( d.getId().equals(id) ) return d;
		}
		
		return null;
	}
	
	/**
	 * Call to refresh all doors and door types. Call after generating new
	 * doors.
	 */
	public static void refreshDoors() {
		NodeReference node = Context.getContext().getAssetNode(DoorType.ASSET_CLASS, null);
		node.appendSubscript("");
		
		doors = new ArrayList<Door>();
		String door = node.nextSubscript();
		while( door != null && door.length() > 0 ) {
			node.setSubscript(node.getSubscriptCount(), door);
			
			logger.trace("Loading Door: {} ...", door);
			DoorType doorType = DoorType.getDoorType(node.getString("type"));
			if( doorType == null ) throw new IllegalStateException("Door [" + door + "] with an invalid door type [" + node.getString("type") + "] found");
			doors.add(new Door(doorType, door));
		
			door = node.nextSubscript();
		}
		
		node.close();
	}
}
