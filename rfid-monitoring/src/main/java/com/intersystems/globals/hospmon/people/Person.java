package com.intersystems.globals.hospmon.people;

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

public class Person extends Asset<PersonMovement, PersonType> {
	private static final Logger logger = LoggerFactory.getLogger(Person.class);

	/** Static holds all people */
	private static List<Person> people;
	
	/** The total number of equipment passing with this person (not neccessarily linked) */
	private int totalEquipmentPeriod;
	
	public Person(PersonType type, String id) {
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
	public Person(PersonType type, String id, boolean save) {
		this(type, id);
		
		if( save ) saveAsset();
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Person [");
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
		Context.updateAverage(node, totalEquipmentPeriod, node.exists("periods") ? node.getInt("periods") : 0, "averageEquipment");
		Context.updateTotal(node, totalEquipmentPeriod, "totalEquipment");
		totalEquipmentPeriod = 0;
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
		for( String person : currentReading.getEquipment() ) lst.append(person);
		node.set(lst, "equipment");
		totalEquipmentPeriod += currentReading.getEquipment().size();
	}
	
	@Override
	protected PersonMovement loadReadingDerived(NodeReference node, Date time) {
		String door = node.getString("door");
		PersonMovement reading = new PersonMovement(getAssetType(), getId(), time, door);
		
		ValueList lst = node.getList("equipment");
		Object[] assets = lst != null ? lst.getAll() : null;
		if( assets != null ) {
			for( Object asset : assets ) {
				reading.addEquipment(asset.toString());
			}
		}
		
		return reading;
	}
	
	//////////////////////////////////////////////////
	// Methods for creating readings
	
	public void addReading(String door, Date time) {
		if( currentReading != null ) throw new RuntimeException("Person seems to have gone through two doors at the same time!!");
		
		currentReading = new PersonMovement(getAssetType(), getId(), time, door);
	}
	
	//////////////////////////////////////////////////
	// Static Methods to get at assets
	
	/**
	 * @return Returns all doors
	 */
	public static List<Person> getAllPeople() {
		return people;
	}
	
	/**
	 * @return The door requested
	 */
	public static Person getPerson(String id) {
		for( Person p : people ) {
			if( p.getId().equals(id) ) return p;
		}
		
		return null;
	}
	
	/**
	 * Call to refresh all doors and door types. Call after generating new
	 * doors.
	 */
	public static void refreshPeople() {
		NodeReference node = Context.getContext().getAssetNode(PersonType.ASSET_CLASS, null);
		node.appendSubscript("");
		
		people = new ArrayList<Person>();
		String personId = node.nextSubscript();
		while( personId != null && personId.length() > 0 ) {
			node.setSubscript(node.getSubscriptCount(), personId);
			
			logger.trace("Loading Person: {} ...", personId);
			PersonType personType = PersonType.getPersonType(node.getString("type"));
			if( personType == null ) throw new IllegalStateException("Person [" + personId + "] with an invalid person type [" + node.getString("type") + "] found");
			people.add(new Person(personType, personId));
		
			personId = node.nextSubscript();
		}
		
		node.close();
	}
}
