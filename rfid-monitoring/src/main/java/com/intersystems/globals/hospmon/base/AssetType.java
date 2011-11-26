package com.intersystems.globals.hospmon.base;

import com.intersys.globals.NodeReference;
import com.intersystems.globals.hospmon.persistence.Context;

/**
 * Base class for a type of asset
 * 
 * @author tspencer
 */
public abstract class AssetType {

	/** The name of the asset type */
	public final String name;
	/** The period for this asset in milliseconds (not absolute) */
	private long period;
	
	/** The min readings per period expected */
	private int minReadingsPerPeriod;
	/** The max readings per period expected */
	private int maxReadingsPerPeriod;
	
	public AssetType(String name) {
		this.name = name;
		loadAssetType();
	}
	
	/**
	 * Construct a new asset type
	 * 
	 * @param name
	 * @param period
	 * @param minReadings
	 * @param maxReadings
	 */
	public AssetType(String name, boolean loadOrCreate, long period, int minReadings, int maxReadings) {
		this.name = name;
		
		if( !loadOrCreate || !loadAssetType() ) {
			this.period = period;
			this.minReadingsPerPeriod = minReadings;
			this.maxReadingsPerPeriod = maxReadings;
		}
	}
	
	/**
	 * @return The type name (i.e. root of where these types are stored)
	 */
	public abstract String getTypeName();
	
	/**
	 * @return The type of this asset
	 */
	public abstract String getType();
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the period
	 */
	public long getPeriod() {
		return period;
	}
	
	/**
	 * @return the minReadingsPerPeriod
	 */
	public int getMinReadingsPerPeriod() {
		return minReadingsPerPeriod;
	}
	
	/**
	 * @return the maxReadingsPerPeriod
	 */
	public int getMaxReadingsPerPeriod() {
		return maxReadingsPerPeriod;
	}
	
	public final boolean loadAssetType() {
		NodeReference node = Context.getContext().getAssetTypeNode(getTypeName(), getName());
		
		boolean ret = node.hasSubnodes();
		try {
			if( ret ) {
				this.period = node.getLong("period");
				this.minReadingsPerPeriod = node.getInt("minReadings");
				this.maxReadingsPerPeriod = node.getInt("maxReadings");
			}
		}
		finally {
			node.close();
		}
		
		return ret;
	}
	
	protected void loadDerived(NodeReference node) {
		
	}
	
	public final void saveAssetType() {
		NodeReference node = Context.getContext().getAssetTypeNode(getTypeName(), getName());
		
		try {
			node.set(getPeriod(), "period");
			node.set(getMinReadingsPerPeriod(), "minReadings");
			node.set(getMaxReadingsPerPeriod(), "maxReadings");
		}
		finally {
			node.close();
		}
	}
	
	protected void saveDerived(NodeReference node) {
		
	}
}
