package com.intersystems.globals.hospmon.equipment;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.NodeReference;
import com.intersystems.globals.hospmon.base.AssetType;
import com.intersystems.globals.hospmon.persistence.Context;

public class EquipmentType extends AssetType {
	private static final Logger logger = LoggerFactory.getLogger(EquipmentType.class);
	
	public static final String ASSET_CLASS = "equipment";
	public static final String ASSET_TYPE = "equipTypes";
	
	/** Static holds all equipment types */
	private static Map<String, EquipmentType> equipmentTypes;
	
	public EquipmentType(String name) {
		super(name);
	}

	/**
	 * Construct a brand new equipment type
	 * 
	 * @param name
	 * @param period
	 * @param minReadings
	 * @param maxReadings
	 */
	public EquipmentType(String name, boolean loadOrCreate, long period, int minReadings, int maxReadings) {
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
		buf.append("EquipmentType [");
		buf.append("name=").append(name);
		buf.append(", minReadings=").append(getMinReadingsPerPeriod());
		buf.append(", maxReadings=").append(getMaxReadingsPerPeriod());
		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * @return Returns all equipment types
	 */
	public static Map<String, EquipmentType> getEquipmentTypes() {
		return equipmentTypes;
	}
	
	public static EquipmentType getEquipmentType(String id) {
		return equipmentTypes != null ? equipmentTypes.get(id) : null;
	}
	
	/**
	 * Call to refresh (reload) all equipment types. This also causes
	 * all equipment to be refreshed (reloaded) as well.
	 */
	public static void refreshEquipmentTypes() {
		NodeReference node = Context.getContext().getAssetTypeNode(ASSET_TYPE, null);
		
		try {
			node.appendSubscript("");
			
			// a. Get the equipment types
			equipmentTypes = new HashMap<String, EquipmentType>();
			String type = node.nextSubscript();
			while( type != null && type.length() > 0 ) {
				logger.trace("Loading EquipmentType: {} ...", type);
				equipmentTypes.put(type, new EquipmentType(type));
				
				node.setSubscript(node.getSubscriptCount(), type);
				type = node.nextSubscript();
			}
		}
		finally {
			node.close();
		}
		
		// Ensure the equipment get re-loaded and linked back to type!
		Equipment.refreshEquipment();
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
