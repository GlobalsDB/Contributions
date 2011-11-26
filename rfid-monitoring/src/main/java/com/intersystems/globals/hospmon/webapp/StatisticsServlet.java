package com.intersystems.globals.hospmon.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intersystems.globals.hospmon.base.Asset;
import com.intersystems.globals.hospmon.base.LinkWriter;
import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.people.Person;

/**
 * This very simple servlet serves up statistics for an asset,
 * all assets, or a type of asset. 
 * 
 * @author tspencer
 */
public class StatisticsServlet extends BaseServlet {
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
		if( "all".equals(cls) ) cls = null;
		String type = getStringValue(req.getParameter("type"));
		String id = getStringValue(req.getParameter("id"));
		String clear = getStringValue(req.getParameter("clear"));
		
		startHtml(resp, "Statistics", 0);
		initialForm(resp.getWriter(), cls, type, id);
		writeStatistics(resp.getWriter(), new HtmlLinkWriter(), cls, type, id, "true".equals(clear));
		endHtml(resp.getWriter());
	}
	
	/**
	 * Displays the header to determine what to display
	 */
	public void initialForm(PrintWriter writer, String cls, String type, String id) {
		writer.append("\n");
		writer.append("<h3>Statistics</h3>");
		
		writer.append("<p>This page displays any statistics for an Asset type or specific Asset. ");
		writer.append("Although not a drop down to support custom types, the asset types are. ");
		writer.append("'MainDoor', 'CorridorDoor', 'RoomDoor', 'Doctor', 'Nurse' or 'Assistant'.</p>");
		
		writer.append("<form action=\"statistics\" method=\"get\">");
		writeSelectInput(writer, "Class", "cls", cls, "all", "", assetClasses);
		writeTextInput(writer, "Type *", "type", type);
		writeTextInput(writer, "Name *", "id", id);
		writeCheck(writer, "Clear", "clear", false);
		writeSubmit(writer);
		writer.append("</form>");
		writer.append("<hr />\n");
		writer.flush();
	}
	
	public void writeStatistics(PrintWriter writer, LinkWriter linkWriter, String cls, String type, String id, boolean clearFirst) {
		int maxTargets = 500;
		List<Asset<?, ?>> target = new ArrayList<Asset<?,?>>();
		
		// Check doors
		List<Door> doors = Door.getDoors();
		int totalAssets = doors.size();
		if( cls == null || "doors".equals(cls) ) {
			for( Door d : doors ) {
				if( (type == null || d.getAssetType().getName().startsWith(type)) &&
						(id == null || d.getId().startsWith(id)) ) target.add(d);
			}
		}
		
		// Check people
		List<Person> people = Person.getAllPeople();
		totalAssets += people.size();
		if( target.size() < maxTargets && cls == null || "people".equals(cls) ) {
			for( Person p : people ) {
				if( (type == null || p.getAssetType().getName().startsWith(type)) &&
						(id == null || p.getId().startsWith(id)) ) target.add(p);
			}
		}
		
		// Check equipment
		List<Equipment> equipment = Equipment.getAllEquipment();
		totalAssets += equipment.size();
		if( target.size() < maxTargets && cls == null || "equipment".equals(cls) ) {
			for( Equipment e : equipment ) {
				if( (type == null || e.getAssetType().getName().startsWith(type)) &&
						(id == null || e.getId().startsWith(id)) ) target.add(e);
			}
		}

		// Display totals
		if( clearFirst ) {
			for( Asset<?, ?> a : target ) a.clearStats();
		}
		
		// Now output
		writer.append("<h4>Statistics</h4>");
		writer.append("<p>Display statistics for " + target.size() + " assets out of " + (doors.size() + people.size()) + " assets in total</p>");
		boolean even = true;
		for( Asset<?, ?> a : target ) {
			writeStatistic(writer, linkWriter, a, even);
			even = !even;
		}
		writer.flush();
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
