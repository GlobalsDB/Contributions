package com.intersystems.globals.hospmon.persistence;

import com.intersys.globals.Connection;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/**
 * This singleton object provides access to the connection.
 * 
 * <p>Note: This is not technically needed, but provides an
 * level abstraction from Globals at least in the interfaces.</p>
 * 
 * @author tspencer
 */
public class Context {
	/** Single (per classloader) instance of Context */
	private static final Context INSTANCE = new Context();
	
	/** The Globals connection */
	private Connection connection;
	
	/**
	 * Hidden constructor
	 */
	private Context() {
	}
	
	/**
	 * @return The singleton Context instance for classloader
	 */
	public static Context getContext() {
		return INSTANCE;
	}
	
	public void init(Connection connection) {
		this.connection = connection;
	}

	public void terminate() {
		connection.close();
	}
	
	public ValueList getValueList() {
		return connection.createList();
	}

	/**
	 * @return The root node of the types for the asset class or the node to specific type
	 */
	public NodeReference getAssetTypeNode(String cls, String assetType) {
		NodeReference node = connection.createNodeReference(cls);
		if( assetType != null ) node.appendSubscript(assetType);
		return node;
	}
	
	/**
	 * @return The root node of the assets of given type or the node to specific asset 
	 */
	public NodeReference getAssetNode(String type, String assetId) {
		NodeReference node = connection.createNodeReference(type);
		if( assetId != null ) node.appendSubscript(assetId);
		return node;
	}
	
	/**
	 * @return The root of all statistics for the asset
	 */
	public NodeReference getAssetStatisticsNode(String type, String assetId) {
		NodeReference node = getAssetNode(type, assetId);
		node.appendSubscript("stats");
		return node;
	}
	
	/**
	 * @return The root of all readings or the specific reading for asset
	 */
	public NodeReference getAssetReadingsNode(String type, String assetId, long reading) {
		NodeReference node = getAssetNode(type, assetId);
		node.appendSubscript("readings");
		if( reading > 0 ) node.appendSubscript(reading);
		return node;
	}
	
	/**
	 * Helper to increment a total by 1
	 * 
	 * @param node
	 * @param subscripts
	 * @return
	 */
	public static int incrementTotal(NodeReference node, Object... subscripts) {
		int last = node.exists(subscripts) ? node.getInt(subscripts) : 0;
		last = last + 1;
		node.set(last, subscripts);
		return last;
	}

	/**
	 * Helper to read an average and update it with the latest
	 * reading.
	 * 
	 * @param node
	 * @param latest
	 * @param periods
	 * @param subscripts
	 * @return
	 */
	public static int updateAverage(NodeReference node, int latest, int periods, Object... subscripts) {
		int last = node.exists(subscripts) ? node.getInt(subscripts) : 0;

		// Update the average, but only consider previous entry to be 5 entries long
		// Could improve further with holding last 5 figures in globals.
		int ret = latest;
		if( last > 0 ) {
			ret = ((last * 5) + latest) / 6;
		}
		
		node.set(ret, subscripts);
		return ret;
	}
	
	/**
	 * Helper to update a total static figure
	 * 
	 * @param node The node
	 * @param latest The latest number
	 * @param subscripts The subscripts under node
	 * @return
	 */
	public static int updateTotal(NodeReference node, int latest, Object... subscripts) {
		int prev = node.exists(subscripts) ? node.getInt(subscripts) : 0;
		prev += latest;
		node.set(prev, subscripts);
		return prev;
	}
}
