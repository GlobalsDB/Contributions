package com.intersystems.globals.hospmon.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This base class represent a reading from some asset.
 * The derived class is expected to hold additional information
 * about this reading.
 * 
 * @author tspencer
 */
public abstract class Reading<T extends AssetType> {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

	/** The type of asset we are a reading of */
	private final T assetType;
	/** The asset we are a reading for */
	private final String assetId;
	/** The date/time the reading was taken */
	private final Date time;
	
	public Reading(T assetType, String asset, Date time) {
		this.assetType = assetType;
		this.assetId = asset;
		this.time = time;
	}
	
	/**
	 * @return the assetType
	 */
	public T getAssetType() {
		return assetType;
	}
	
	/**
	 * @return the assetId
	 */
	public String getAssetId() {
		return assetId;
	}
	
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * @return A list of name of all the reading values - derived class should override
	 */
	public final List<String> getReadingValueNames() {
		List<String> ret = new ArrayList<String>();
		ret.add("Time");
		addReadingValueNames(ret);
		return ret;
	}
	
	protected abstract void addReadingValueNames(List<String> names);
	
	/**
	 * @param linkWriter The {@link LinkWriter} to use to have links to other assets/asset types
	 * @return A map of all reading values keyed against the value name - derived class should override
	 */
	public final Map<String, String> getReadingValues(LinkWriter linkWriter) {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("Time", sdf.format(time));
		addReadingValues(linkWriter, ret);
		return ret;
	}
	
	protected abstract void addReadingValues(LinkWriter linkWriter, Map<String, String> values);
}
