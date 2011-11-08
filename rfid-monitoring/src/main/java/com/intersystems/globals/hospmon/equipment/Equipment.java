package com.intersystems.globals.hospmon.equipment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.intersystems.globals.hospmon.base.Asset;
import com.intersystems.globals.hospmon.persistence.Context;

public class Equipment extends Asset<EquipmentMovement, EquipmentType> {
	private static final Logger logger = LoggerFactory.getLogger(Equipment.class);
	
	/** Static holds all equipment */
	private static List<Equipment> equipments;
	
	/** The total number of people passing with this piece of equipment (not neccessarily linked) */
	private int totalPeoplePeriod;
	
	public Equipment(EquipmentType type, String id) {
		super(type, id);
		loadAsset();
	}
	
	/**
	 * Constructs a new instance an optionally saves it.
	 * 
	 * @param door
	 * @param id
	 * @param save
	 */
	public Equipment(EquipmentType type, String id, boolean save) {
		this(type, id);
		
		if( save ) saveAsset();
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Equipment [");
		buf.append("type=").append(getAssetType().getType());
		buf.append(", id=").append(getId());
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
	}
	
	@Override
	protected void saveDerived(NodeReference node) {
	}
	
	@Override
	protected void updateDerived(NodeReference node) {
		Context.updateAverage(node, totalPeoplePeriod, node.exists("periods") ? node.getInt("periods") : 0, "averagePeople");
		Context.updateTotal(node, totalPeoplePeriod, "totalPeople");
		totalPeoplePeriod = 0;
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
		node.set(currentReading.getDoor(), "door");
		ValueList lst = Context.getContext().getValueList();
		for( String person : currentReading.getPeople() ) lst.append(person);
		node.set(lst, "people");
		totalPeoplePeriod += currentReading.getPeople().size();
	}
	
	@Override
	protected EquipmentMovement loadReadingDerived(NodeReference node, Date time) {
		String door = node.getString("door");
		EquipmentMovement reading = new EquipmentMovement(getAssetType(), getId(), time, door);
		
		ValueList lst = node.getList("people");
		Object[] assets = lst != null ? lst.getAll() : null;
		if( assets != null ) {
			for( Object asset : assets ) {
				reading.addPerson(asset.toString());
			}
		}
		
		return reading;
	}
	
	//////////////////////////////////////////////////
	// Methods for creating readings
	
	public void addReading(String door, Date time) {
		if( currentReading != null ) throw new RuntimeException("Equipment [" + getId() + "] seems to have gone through two doors at the same time!!");
		
		currentReading = new EquipmentMovement(getAssetType(), getId(), time, door);
	}
	
	//////////////////////////////////////////////////
	// Static Methods to get at assets
	
	/**
	 * @return Returns all doors
	 */
	public static List<Equipment> getAllEquipment() {
		return equipments;
	}
	
	/**
	 * @return The door requested
	 */
	public static Equipment getEquipment(String id) {
		for( Equipment e : equipments ) {
			if( e.getId().equals(id) ) return e;
		}
		
		return null;
	}
	
	/**
	 * Call to refresh all doors and door types. Call after generating new
	 * doors.
	 */
	public static void refreshEquipment() {
		NodeReference node = Context.getContext().getAssetNode(EquipmentType.ASSET_CLASS, null);
		node.appendSubscript("");
		
		equipments = new ArrayList<Equipment>();
		String equipId = node.nextSubscript();
		while( equipId != null && equipId.length() > 0 ) {
			node.setSubscript(node.getSubscriptCount(), equipId);
			
			logger.trace("Loading Equipment: {} ...", equipId);
			EquipmentType equipmentType = EquipmentType.getEquipmentType(node.getString("type"));
			if( equipmentType == null ) throw new IllegalStateException("Equipment [" + equipId + "] with an invalid door type [" + node.getString("type") + "] found");
			equipments.add(new Equipment(equipmentType, equipId));
		
			equipId = node.nextSubscript();
		}
		
		node.close();
	}
}
