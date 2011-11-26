package com.intersystems.globals.hospmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.people.Person;

/**
 * Holds all the collectors running
 * 
 * @author tspencer
 */
public class MockCollectors {
	private final static Logger logger = LoggerFactory.getLogger(MockCollector.class);
	private final static MockCollectors INSTANCE = new MockCollectors();
	
	private MockCollectors() {
		
	}
	
	public static MockCollectors getInstance() {
		return INSTANCE;
	}
	
	private List<MockCollector> collectors;
	private MockDoorReadingRunner doorReadingRunner;
	
	public boolean isRunning() {
		return collectors != null;
	}
	
	public void start(int targetRate) {
		if( isRunning() ) throw new IllegalStateException("Already running!!");
		
		List<Thread> threads = new ArrayList<Thread>();
		collectors = new ArrayList<MockCollector>();
		doorReadingRunner = new MockDoorReadingRunner();
		
		List<Door> doors = Door.getDoors();
		List<Person> people = new ArrayList<Person>(Person.getAllPeople());
		List<Equipment> equip = new ArrayList<Equipment>(Equipment.getAllEquipment());
		
		int maxCollectors = ((people.size() / 2) / 100);
		int numCollectors = (targetRate / 100);
		if( targetRate % 100 > 0 ) numCollectors += 1;
		if( numCollectors > maxCollectors ) numCollectors = maxCollectors;
		
		if( people.size() < (targetRate * 2) ) numCollectors = (people.size() / 2) / 100;
		int ratePerCollector = targetRate / numCollectors;
		
		for( int i = 0 ; i < numCollectors ; i++ ) {
			List<Person> collectorPeople = new ArrayList<Person>(200);
			List<Equipment> collectorEquip = new ArrayList<Equipment>(2000);
			
			Random r = new Random(System.nanoTime());
			while( collectorPeople.size() < 200 && people.size() > 0 ) collectorPeople.add(people.remove(r.nextInt(people.size())));
			while( collectorEquip.size() < 2000 && equip.size() > 0 ) collectorEquip.add(equip.remove(r.nextInt(equip.size())));
			
			if( (collectorPeople.size() + collectorEquip.size()) / 10 > ratePerCollector ) {
				MockCollector c = new MockCollector(ratePerCollector, doors, collectorPeople, collectorEquip);
				collectors.add(c);
				threads.add(new Thread(c));
			}
		}
		
		// Saves statistics away
		threads.add(new Thread(doorReadingRunner));
		
		for( Thread t : threads ) t.start();
		
		logger.info("{} Mock collectors created + 1 management thread", collectors.size());
	}
	
	public CollectionStats stop() {
		if( !isRunning() ) throw new IllegalStateException("Cannot stop as not running");
		
		CollectionStats ret = getCollectionStats();
		
		for( MockCollector c : collectors ) c.stop();
		doorReadingRunner.stop();
		
		collectors = null;
		doorReadingRunner = null;
		
		try {
			Thread.sleep(1000); // Wait for them to stop
		}
		catch( Exception e ) {}
		
		logger.info("Mock collectors stopped, final rate was {}", ret.getReadingsPerSecond());
		return ret;
	}

	/**
	 * Call to get the readings per second and save time
	 * for that rate.
	 * 
	 * @return The stats
	 */
	public CollectionStats getCollectionStats() {
		int readings = 0;
		int saveTime = 0;
		
		for( MockCollector c : collectors ) {
			CollectionStats s = c.getCollectionStats();
			readings += s.getReadingsPerSecond();
			saveTime += s.getSaveTimePerSecond();
		}

		saveTime /= collectors.size();
		return new CollectionStats(1000, readings, saveTime);
	}
}
