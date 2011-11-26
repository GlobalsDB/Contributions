package com.intersystems.globals.hospmon.webapp;

import com.intersystems.globals.hospmon.base.LinkWriter;

public class HtmlLinkWriter implements LinkWriter {
	
	private StringBuilder tempBuffer;
	
	private StringBuilder getTempBuffer() {
		if( tempBuffer == null ) tempBuffer = new StringBuilder();
		else tempBuffer.setLength(0);
		
		return tempBuffer;
	}

	public String linkAsset(String assetClass, String assetId, String text) {
		StringBuilder buf = getTempBuffer();
		buf.append("<a href=\"");
		buf.append("/readings?cls=").append(assetClass).append("&id=").append(assetId);
		buf.append("\">").append(text).append("</a>");
		
		return buf.toString();
	}
	
	public String linkAssetType(String assetClass, String assetType, String text) {
		StringBuilder buf = getTempBuffer();
		buf.append("<a href=\"");
		buf.append("/statistics?cls=").append(assetClass).append("&type=").append(assetType);
		buf.append("\">").append(text).append("</a>");
		
		return buf.toString();
	}
}
