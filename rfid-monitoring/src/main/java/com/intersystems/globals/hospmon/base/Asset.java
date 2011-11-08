package com.intersystems.globals.hospmon.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.NodeReference;
import com.intersystems.globals.hospmon.persistence.Context;

/**
 * This base class represents an instance of an asset
 * that we are monitoring. Every time a reading on this
 * asset is recorded the general stats are updated of
 * the asset. Separate thread then comes around and asks
 * asset whether it wants to save itself, which is does
 * only during the relevant periods.
 * 
 * @author tspencer
 */
public abstract class Asset<R extends Reading<T>, T extends AssetType> {
	private static final Logger logger = LoggerFactory.getLogger(Asset.class);

	/** The asset type we belong to */
	private T assetType;
	/** The id of the asset */
	private String id;
	
	/** Holds the current reading */
	protected R currentReading;
	
	/** The date and time the averages we last saved */
	private long lastSave = System.currentTimeMillis();
	/** The date/time of last reading */
	private Date lastReading;
	/** The number of readings/usages in last set */
	private int nosReadings;
	
	public Asset(T assetType, String id) {
		if( id == null ) throw new IllegalArgumentException("Must have a valid asset id");
		if( assetType == null ) throw new IllegalArgumentException("Must have a valid asset type");
		
		this.assetType = assetType;
		this.id = id;
		
		this.lastSave = System.currentTimeMillis();
	}
	
	/**
	 * @return the assetType
	 */
	public T getAssetType() {
		return assetType;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the lastReading
	 */
	public Date getLastReading() {
		return lastReading;
	}

	/**
	 * @param lastReading the lastReading to set
	 */
	public void setLastReading(Date lastReading) {
		this.lastReading = lastReading;
	}

	/**
	 * @return the nosReadings
	 */
	public int getNosReadings() {
		return nosReadings;
	}

	/**
	 * @param nosReadings the nosReadings to set
	 */
	public void setNosReadings(int nosReadings) {
		this.nosReadings = nosReadings;
	}

	/**
	 * @return the lastSave
	 */
	public long getLastSave() {
		return lastSave;
	}

	public final void loadAsset() {
		NodeReference node = Context.getContext().getAssetNode(assetType.getType(), id);
		
		try {
			loadDerived(node);
		}
		finally {
			node.close();
		}
	}
	
	protected abstract void loadDerived(NodeReference node);
	
	public final void saveAsset() {
		NodeReference node = Context.getContext().getAssetNode(assetType.getType(), id);
		
		try {
			node.set(assetType.getName(), "type");
			saveDerived(node);
		}
		finally {
			node.close();
		}
	}
	
	protected abstract void saveDerived(NodeReference node);
	
	public final List<String> getStatisticNames() {
		List<String> ret = new ArrayList<String>();
		ret.add("Periods");
		ret.add("AverageReadings");
		ret.add("TotalReadings");
		ret.add("MinReadings");
		ret.add("MaxReadings");
		return ret;
	}
	
	protected abstract void addDerivedStatNames(List<String> stats);
	
	public final Map<String, String> getStatistics() {
		NodeReference node = Context.getContext().getAssetStatisticsNode(assetType.getType(), id);
		
		Map<String, String> ret = new HashMap<String, String>();
		
		try {
			ret.put("Periods", node.exists("periods") ? Integer.toString(node.getInt("periods")) : "0");
			ret.put("AverageReadings", node.exists("averageReadings") ? Integer.toString(node.getInt("averageReadings")) : "0");
			ret.put("TotalReadings", node.exists("totalReadings") ? Integer.toString(node.getInt("totalReadings")) : "0");
			
			getStatisticsDerived(node, ret);
		}
		finally {
			node.close();
		}
		
		return ret;
	}
	
	protected abstract void getStatisticsDerived(NodeReference node, Map<String, String> stats);
	
	/**
	 * Called to allow the asset to allow it to save it's statistics
	 */
	public final void updateStatistics() {
		if( !shouldSave() ) return;
		
		NodeReference node = Context.getContext().getAssetStatisticsNode(assetType.getType(), id);
		
		try {
			int total = Context.incrementTotal(node, "periods");
			Context.updateAverage(node, getNosReadings(), total, "averageReadings");
			Context.updateTotal(node, getNosReadings(), "totalReadings");
			
			updateDerived(node);
			setNosReadings(0);
			
			logger.info("Saved Stats {}", this);
		}
		finally {
			node.close();
		}
	}
	
	protected abstract void updateDerived(NodeReference node);
	
	/**
	 * Clear all statistics
	 */
	public final void clearStats() {
		NodeReference node = Context.getContext().getAssetStatisticsNode(assetType.getType(), id);
		
		try {
			node.kill();
		}
		finally {
			node.close();
		}
	}
	
	/**
	 * @return Returns the current reading
	 */
	public R getCurrentReading() {
		return currentReading;
	}
	
	public final void saveCurrentReading() {
		if( currentReading == null ) return;
		
		synchronized(this) {
			NodeReference node = Context.getContext().getAssetReadingsNode(assetType.getType(), id, currentReading.getTime().getTime());
		
			try {
				// TODO
				
				saveReadingDerived(node);
				
				logger.trace("Saved Reading {}", currentReading);
			}
			finally {
				node.close();
			}
			
			lastReading = currentReading.getTime();
			nosReadings += 1;
			currentReading = null;
		}
	}
	
	protected abstract void saveReadingDerived(NodeReference node);
	
	/**
	 * Called to get the readings for this asset
	 * 
	 * @param from The first reading to get (0 means last one recorded)
	 * @param total The total number of readings to get
	 * @return
	 */
	public final List<R> getReadings(int from, int total) {
		NodeReference node = Context.getContext().getAssetReadingsNode(assetType.getType(), id, 0);
		
		List<R> ret = new ArrayList<R>();
		try {
			node.appendSubscript("");
			String sub = node.previousSubscript();
			int considered = 0;
			while( ret.size() < total && sub != null && sub.length() > 0 ) {
				node.setSubscript(node.getSubscriptCount(), sub);
				
				if( considered >= from ) {
					Date time = new Date(Long.parseLong(sub));
					R reading = loadReadingDerived(node, time);
					ret.add(reading);
				}
				
				sub = node.previousSubscript();
				++considered;
			}
		}
		finally {
			node.close();
		}
		
		return ret;
	}
	
	protected abstract R loadReadingDerived(NodeReference node, Date time);
	
	/**
	 * @return True if the asset should save itself
	 */
	private boolean shouldSave() {
		// Must have had a reading to be considered
		if( this.nosReadings == 0 ) return false;
		
		long current = System.currentTimeMillis();
		boolean ret = (current - lastSave) > (assetType.getPeriod() * 1000);
		if( ret ) {
			lastSave = current;
		}
		return ret;
	}
}
