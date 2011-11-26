package com.intersystems.globals.hospmon.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intersystems.globals.hospmon.base.Asset;
import com.intersystems.globals.hospmon.base.LinkWriter;
import com.intersystems.globals.hospmon.base.Reading;
import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.people.Person;

/**
 * This very simple servlet serves up statistics for an asset,
 * all assets, or a type of asset. 
 * 
 * @author tspencer
 */
public class ReadingsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Map<String, String> assetClasses;
	
	static {
		assetClasses = new HashMap<String, String>();
		assetClasses.put("doors", "Door");
		assetClasses.put("people", "People");
		assetClasses.put("equipment", "Equipment");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String cls = getStringValue(req.getParameter("cls"));
		String id = getStringValue(req.getParameter("id"));
		
		startHtml(resp, "Readings", 0);
		writeReadings(resp.getWriter(), new HtmlLinkWriter(), cls, id);
		endHtml(resp.getWriter());
	}
	
	/**
	 * Helper to write out the last 100 (configurable) readings for this asset
	 */
	public void writeReadings(PrintWriter writer, LinkWriter linkWriter, String cls, String id) {
		Asset<?, ?> asset = null;
		
		if( cls == null || "doors".equals(cls) ) asset = Door.getDoor(id);
		if( asset == null && (cls == null || "people".equals(cls)) ) asset = Person.getPerson(id);
		if( asset == null && (cls == null || "equipment".equals(cls)) ) asset = Equipment.getEquipment(id);
		
		if( asset == null ) {
			writer.append("<p>There is no asset to display the readings for, or the asset is not found</p>");
			writer.flush();
		}
		else {
			writer.append("<p>Display for '" + asset.getId() + "'</p>");
			
			// Statistics
			writer.append("<h4>Statistics</h4>");
			writeStatistic(writer, linkWriter, asset, true);
			
			writer.append("<br />");
			writer.append("<hr />");
			
			// Readings
			writer.append("<h4>Readings</h4>");
			
			boolean even = true;
			List<? extends Reading<?>> readings = asset.getReadings(0, 100); // TODO: Need to make this configurable!
			for( Reading<?> r : readings ) {
				writeReading(writer, linkWriter, r, even);
				even = !even;
			}
			
			writer.flush();
		}
		
		
	}
	
	public void writeReading(PrintWriter writer, LinkWriter linkWriter, Reading<?> reading, boolean even) {
		List<String> valueNames = reading.getReadingValueNames();
		Map<String, String> values = reading.getReadingValues(linkWriter);
		
		String divClass = even ? "reading even" : "reading odd";
		writer.append("\n<div class=\"").append(divClass).append("\">");
		
		for( String value : valueNames ) {
			writer.append("<span class='value ").append(value).append("'>");
			writer.append("<span class='heading'>").append(value).append("</span>");
			writer.append(values.get(value));
			writer.append("</span>");
			
			even = !even;
		}
		
		writer.append("</div>");
	}
	
	public void writeStatistic(PrintWriter writer, LinkWriter linkWriter, Asset<?, ?> asset, boolean even) {
		Map<String, String> stats = asset.getStatistics();
		
		String divClass = even ? "stat even" : "stat odd";
		writer.append("\n<div class=\"").append(divClass).append("\">");
		writer.append("<span class=\"asset\">").append(linkWriter.linkAsset(asset.getAssetType().getType(), asset.getId(), asset.getId())).append("</span>");
		
		for( String stat : stats.keySet() ) {
			writer.append("<span title=\"").append(stat).append("\" class=\"value ").append(stat).append("\">");
			writer.append("<span class=\"heading\">").append(stat).append("</span>");
			writer.append(stats.get(stat));
			writer.append("</span>");
		}
		writer.append("</div>");
	}
}
