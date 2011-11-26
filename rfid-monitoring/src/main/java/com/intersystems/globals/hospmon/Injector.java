package com.intersystems.globals.hospmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.intersystems.globals.hospmon.base.Asset;
import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.people.Person;
import com.intersystems.globals.hospmon.persistence.Context;

/**
 * This class is a mock injector that can be run to inject in
 * readings.
 * 
 * @author tspencer
 */
@Deprecated
public class Injector {
	
	/** The doors we can go through */
	private List<Door> doors;
	/** The people in our building */
	private List<Person> people;
	
	/** The target rate of updates per second */
	private int ratePerSecond;
	/** The total time to run for */
	private int totalTime;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int rate = 200;
		int seconds = 10;
		
		for( String arg : args ) {
			try {
				if( arg.startsWith("-rate=") ) rate = Integer.parseInt(arg.substring(6));
				else if( arg.startsWith("-seconds=") ) seconds = Integer.parseInt(arg.substring(9));
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
			Injector injector = new Injector(rate, seconds);
			injector.inject();
		}
		finally {
			Context.getContext().terminate();
		}
	}
	
	/**
	 * Construct the injector.
	 * 
	 * @param ratePerSecond
	 * @param totalTime
	 */
	public Injector(int ratePerSecond, int totalTime) {
		this.ratePerSecond = ratePerSecond;
		this.totalTime = totalTime;
		
		this.doors = Door.getDoors();
		this.people = Person.getAllPeople();
	}
	
	public void refreshContents() {
		this.doors = Door.getDoors();
		this.people = Person.getAllPeople();
	}
	
	public void setRate(int rate) {
		this.ratePerSecond = rate;
	}
	
	/**
	 * Call to inject in random readings
	 */
	public void inject() {
		long start = System.currentTimeMillis();
		long end = start + (totalTime * 1000);
		
		long time = System.currentTimeMillis();
		while( totalTime < 0 || time < end ) {
			long next = time + 1000;
			
			int written = injectOnce();
			
			long newTime = System.currentTimeMillis();
			System.out.print("\rWritten " + written + " - " + (newTime - time));
			time = newTime;
			
			if( time < next ) {
				try {
					Thread.sleep(next - time);
				}
				catch( InterruptedException e ) {
					System.exit(1);
				}
				
				time = System.currentTimeMillis();
			}
		}
	}
	
	public int injectOnce() {
		int rate = ratePerSecond;
		if( rate > people.size() ) rate = people.size();
		Random r = new Random(System.nanoTime());
		
		List<Asset<?, ?>> toSave = new ArrayList<Asset<?,?>>(rate);
		
		for( int i = 0 ; i < rate ; i++ ) {
			Person p = people.get(r.nextInt(people.size()));
			if( p.getCurrentReading() == null ) {
				// Find a door
				Door d = doors.get(r.nextInt(doors.size()));

				boolean use = false;
				while( !use ) {
					int usageChance = d.getUsageChance();
					int seed = r.nextInt(100) + 1;
					if( seed % usageChance == 0 ) use = true;
					if( !use ) d = doors.get(r.nextInt(doors.size()));
				}
				
				d.addToReading(new Person[]{p}, null);
				toSave.add(d);
				toSave.add(p);
			}
		}
		
		// Save all readings
		for( Asset<?, ?> a : toSave ) a.saveCurrentReading();
		
		// Ask assets to save themselves (if they want)!
		for( Door d : doors ) d.saveAsset();
		for( Person p : people ) p.saveAsset();
		
		return toSave.size();
	}
}
