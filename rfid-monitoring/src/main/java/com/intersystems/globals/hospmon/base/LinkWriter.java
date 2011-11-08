package com.intersystems.globals.hospmon.base;

/**
 * This interface allows us to generically write out links for asset types
 * and assets without knowing about the view technology.
 * 
 * @author tspencer
 */
public interface LinkWriter {

	/**
	 * @return A link to the assets types page (it's statistics)
	 */
	public abstract String linkAssetType(String assetClass, String assetType, String text);
	
	/**
	 * @return A link to the assets page (it's readings)
	 */
	public abstract String linkAsset(String assetClass, String assetId, String text);
}
