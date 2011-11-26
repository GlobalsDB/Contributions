package com.intersystems.globals.hospmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.intersystems.globals.hospmon.base.Asset;
import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.people.Person;
import com.intersystems.globals.hospmon.persistence.Context;

/**
 * This command line class print out stats for the given
 * types.
 * 
 * @author tspencer
 */
public class Statistics {
	
	/** The type of asset we are interested in */
	private String type;
	/** The optional ID mask we are interested in */
	private String id;
	/** If true any selected asset stats are cleared first */
	private boolean clearFirst = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String type = "Nurse";
		String id = null;
		boolean clear = false;
		
		for( String arg : args ) {
			try {
				if( arg.startsWith("-type=") ) type = arg.substring(6);
				else if( arg.startsWith("-id=") ) id = arg.substring(4);
				else if( arg.equals("-clear") ) clear = true;
				else {
					System.out.println("Unrecognised argument: " + arg);
					System.exit(2);
				}
			}
			catch( Exception e ) {
				System.out.println("Unable to process argument " + arg + ", reason: " + e.getMessage());
				System.exit(2);
			}
		}
				
		try {
			Statistics stats = new Statistics(type, id, clear);
			stats.generate();
		}
		finally {
			Context.getContext().terminate();
		}
	}
	
	public Statistics(String type, String id, boolean clear) {
		this.type = type;
		this.id = id;
		this.clearFirst = clear;
	}
	
	public void generate() {
		List<Asset<?, ?>> target = new ArrayList<Asset<?,?>>();
		
		// Check doors
		List<Door> doors = Door.getDoors();
		for( Door d : doors ) {
			if( id != null && d.getId().equals(id) ) target.add(d);
			else if( d.getAssetType().getName().equals(type) ) target.add(d);
		}
		
		// Check people
		List<Person> people = Person.getAllPeople();
		for( Person p : people ) {
			if( id != null && p.getId().equals(id) ) target.add(p);
			else if( p.getAssetType().getName().equals(type) ) target.add(p);
		}

		// Display totals
		System.out.println("Display statistics for " + target.size() + " assets out of " + (doors.size() + people.size()) + " assets in total");
		
		if( clearFirst ) {
			for( Asset<?, ?> a : target ) a.clearStats();
		}
		
		// Now output
		StringBuilder buf = new StringBuilder();
		for( Asset<?, ?> a : target ) {
			Map<String, String> stats = a.getStatistics();
			
			buf.setLength(0);
			buf.append(a.getId());
			for( String stat : stats.keySet() ) {
				buf.append(", ").append(stat).append("=").append(stats.get(stat));
			}
			
			System.out.println(buf);
		}
	}
}
