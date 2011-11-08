package com.intersystems.globals.hospmon;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.people.Person;

/**
 * Represents a mock data collector that is providing data
 * into our system. It works best when you give a collector
 * its own people and equipment.
 * 
 * @author tspencer
 */
public class MockCollector implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(MockCollector.class);

	/** The target rate this collector will try and hit (per second) */
	private final int targetRate;
	/** Holds the doors this collector operates on */
	private final List<Door> doors;
	/** Holds the people this collector can use */
	private final List<Person> people;
	/** Holds the equipment this collector uses */
	private final List<Equipment> equipment;
	
	/** The lower amount of people who will pass through any 1 door */
	private int lowerPersonRate = 1;
	/** The upper amount of people who will pass through any 1 door */
	private int upperPersonRate = 3;
	/** The lower amount of equipment who will pass through any 1 door */
	private int lowerEquipmentRate = 2;
	/** The upper amount of equipment who will pass through any 1 door */
	private int upperEquipmentRate = 12;
	
	/** Holds the number of readings since last obtained */
	private int readingsCreated;
	/** Holds the time last read */
	private long timeLastRead;
	/** Holds the total time to save readings */
	private long readingSaveTime;
	
	private boolean keepRunning = true;
	
	public MockCollector(int rate, List<Door> doors, List<Person> people, List<Equipment> equipment) {
		this.targetRate = rate;
		this.doors = doors;
		this.people = people;
		this.equipment = equipment;
	}
	
	public void stop() {
		keepRunning = false;
	}
	
	/**
	 * Main run method that tries to hit the target rate each
	 * second.
	 */
	public void run() {
		keepRunning = true;
		timeLastRead = System.currentTimeMillis();
		
		int iterationRate = people.size() / 2;
		int iterations = targetRate / iterationRate;
		if( iterations < 1 ) iterations = 0;
		
		// Always ensure we are waiting 10ms for other threads
		int wait = 1000 / iterations;
		if( wait < 10 ) wait = 10;
		
		while( keepRunning ) {
			long st = System.currentTimeMillis();
			
			int generated = inject(iterationRate);
			
			// Now save all people and equipment in this run
			long st2 = System.currentTimeMillis();
			for( Person p : people ) p.saveCurrentReading();
			for( Equipment e : equipment ) e.saveCurrentReading();
			
			long en = System.currentTimeMillis();
			readingSaveTime += (en-st2);
			
			long waitTime = 50;
			if( (en-st) < wait ) waitTime = wait-(en-st);
			
			try {
				logger.debug("Generated {} readings in {}", generated, (en-st));
				Thread.sleep(waitTime);
			} 
			catch (InterruptedException e) {
				// Nothing, all ok
				return;
			} 
		}
	}
	
	public CollectionStats getCollectionStats() {
		CollectionStats ret = null;
		
		synchronized(this) {
			long current = System.currentTimeMillis();
			long timeSpan = current - timeLastRead;
			ret = new CollectionStats(timeSpan, readingsCreated, readingSaveTime);
			readingsCreated = 0;
			readingSaveTime = 0;
			timeLastRead = current;
		}
		
		return ret;
	}
	
	public int inject(int targetRate) {
		Random r = new Random(System.nanoTime());
		
		int runs = 0;
		int total = 0;
		while( total < targetRate && runs < (targetRate * 10) ) {
			Door d = doors.get(r.nextInt(doors.size()));
			
			boolean use = false;
			while( !use ) {
				int usageChance = d.getUsageChance();
				int seed = r.nextInt(100) + 1;
				if( seed % usageChance == 0 ) use = true;
				if( !use ) d = doors.get(r.nextInt(doors.size()));
			}
			
			total += addReading(d);
			runs++;
		}
		
		synchronized(this) {
			readingsCreated += total;
		}
		
		return total;
	}
	
	public int addReading(Door door) {
		Random r = new Random(System.nanoTime());
		
		// Find people
		int tests = 0;
		int nosPeople = lowerPersonRate + r.nextInt(upperPersonRate);
		Person[] people = new Person[nosPeople];
		for( int i = 0 ; i < nosPeople ; i++ ) {
			Person p = this.people.get(r.nextInt(this.people.size()));
			
			boolean alreadyAdded = false;
			if( p.getCurrentReading() != null ) alreadyAdded = true;
			else {
				for( int j = 0 ; j < people.length ; j++ ) {
					if( people[j] == p ) {
						alreadyAdded = true;
					}
				}
			}
			
			if( alreadyAdded ) --i;
			else people[i] = p;
			
			tests++;
			if( tests >= (upperPersonRate * 10) ) break;
		}
		
		// Find equipment
		tests = 0;
		int nosEquip = lowerEquipmentRate + r.nextInt(upperEquipmentRate);
		Equipment[] equipment = new Equipment[nosEquip];
		for( int i = 0 ; i < nosEquip ; i++ ) {
			Equipment e = this.equipment.get(r.nextInt(this.equipment.size()));
			
			boolean alreadyAdded = false;
			if( e.getCurrentReading() != null ) alreadyAdded = true;
			else {
				for( int j = 0 ; j < equipment.length ; j++ ) {
					if( equipment[j] == e ) {
						alreadyAdded = true;
					}
				}
			}
			
			if( alreadyAdded ) --i;
			else equipment[i] = e;
			
			tests++;
			if( tests >= (upperEquipmentRate * 10) ) break;
		}
		
		int ret = 1 + nosPeople + nosEquip; 
		door.addToReading(people, equipment);
		
		return ret;
	}
}
