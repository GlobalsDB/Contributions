package com.intersystems.globals.hospmon.doors;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.NodeReference;
import com.intersystems.globals.hospmon.base.AssetType;
import com.intersystems.globals.hospmon.persistence.Context;

public class DoorType extends AssetType {
	private static final Logger logger = LoggerFactory.getLogger(DoorType.class);
	
	public static final String ASSET_CLASS = "doors";
	public static final String ASSET_TYPE = "doorTypes";
	
	/** Static holds all door types */
	private static Map<String, DoorType> doorTypes;
	
	public DoorType(String name) {
		super(name);
	}

	/**
	 * Construct a brand new door type
	 * 
	 * @param name
	 * @param period
	 * @param minReadings
	 * @param maxReadings
	 */
	public DoorType(String name, boolean loadOrCreate, long period, int minReadings, int maxReadings) {
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
		buf.append("DoorType [");
		buf.append("name=").append(name);
		buf.append(", minReadings=").append(getMinReadingsPerPeriod());
		buf.append(", maxReadings=").append(getMaxReadingsPerPeriod());
		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * @return Returns all door types
	 */
	public static Map<String, DoorType> getDoorTypes() {
		return doorTypes;
	}
	
	public static DoorType getDoorType(String id) {
		return doorTypes != null ? doorTypes.get(id) : null;
	}
	
	/**
	 * Call to refresh (reload) all door types. This also causes
	 * all doors to be refreshed (reloaded) as well.
	 */
	public static void refreshDoorTypes() {
		NodeReference node = Context.getContext().getAssetTypeNode(ASSET_TYPE, null);
		
		try {
			node.appendSubscript("");
			
			// a. Get the door types
			doorTypes = new HashMap<String, DoorType>();
			String type = node.nextSubscript();
			while( type != null && type.length() > 0 ) {
				logger.trace("Loading DoorType: {} ...", type);
				doorTypes.put(type, new DoorType(type));
				
				node.setSubscript(node.getSubscriptCount(), type);
				type = node.nextSubscript();
			}
		}
		finally {
			node.close();
		}
		
		// Ensure the doors get re-loaded and linked back to type!
		Door.refreshDoors();
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
