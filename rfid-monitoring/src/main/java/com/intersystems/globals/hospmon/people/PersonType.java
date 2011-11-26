package com.intersystems.globals.hospmon.people;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.NodeReference;
import com.intersystems.globals.hospmon.base.AssetType;
import com.intersystems.globals.hospmon.persistence.Context;

public class PersonType extends AssetType {
	private static final Logger logger = LoggerFactory.getLogger(PersonType.class);
	
	public static final String ASSET_CLASS = "people";
	public static final String ASSET_TYPE = "personTypes";
	
	/** Static holds all person types */
	private static Map<String, PersonType> personTypes;
	
	public PersonType(String name) {
		super(name);
	}

	/**
	 * Construct a brand new person type
	 * 
	 * @param name
	 * @param period
	 * @param minReadings
	 * @param maxReadings
	 */
	public PersonType(String name, boolean loadOrCreate, long period, int minReadings, int maxReadings) {
		super(name, loadOrCreate, period, minReadings, maxReadings);
	}
	
	@Override
	public String getTypeName() {
		return ASSET_TYPE;
	}
	
	@Override
	public String getType() {
		return ASSET_CLASS;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("PersonType [");
		buf.append("name=").append(name);
		buf.append(", minReadings=").append(getMinReadingsPerPeriod());
		buf.append(", maxReadings=").append(getMaxReadingsPerPeriod());
		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * @return Returns all person types
	 */
	public static Map<String, PersonType> getPersonTypes() {
		return personTypes;
	}
	
	public static PersonType getPersonType(String id) {
		return personTypes != null ? personTypes.get(id) : null;
	}
	
	/**
	 * Call to refresh (reload) all person types. This also causes
	 * all people to be refreshed (reloaded) as well.
	 */
	public static void refreshPersonTypes() {
		NodeReference node = Context.getContext().getAssetTypeNode(ASSET_TYPE, null);
		
		try {
			node.appendSubscript("");
			
			// a. Get the person types
			personTypes = new HashMap<String, PersonType>();
			String type = node.nextSubscript();
			while( type != null && type.length() > 0 ) {
				logger.trace("Loading PersonType: {} ...", type);
				personTypes.put(type, new PersonType(type));
				
				node.setSubscript(node.getSubscriptCount(), type);
				type = node.nextSubscript();
			}
		}
		finally {
			node.close();
		}
		
		// Ensure the persons get re-loaded and linked back to type!
		Person.refreshPeople();
	}
	
	public static void removeAll(boolean incTypes) {
		if( incTypes ) {
			NodeReference node = Context.getContext().getAssetTypeNode(ASSET_TYPE, null);
			node.kill();
			node.close();
		}
		
		NodeReference node = Context.getContext().getAssetTypeNode(ASSET_CLASS, null);
		node.kill();
		node.close();
	}
}
