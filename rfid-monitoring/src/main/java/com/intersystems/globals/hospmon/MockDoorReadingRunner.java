package com.intersystems.globals.hospmon;

import java.util.List;

import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.people.Person;

public class MockDoorReadingRunner implements Runnable {
	
	private List<Door> doors;
	private List<Person> people;
	private List<Equipment> equipment;
	
	private boolean keepRunning = true;
	
	public MockDoorReadingRunner() {
		this.doors = Door.getDoors();
		this.people = Person.getAllPeople();
		this.equipment = Equipment.getAllEquipment();
	}
	
	public void stop() {
		keepRunning = false;
	}

	public void run() {
		keepRunning = true;
		
		try {
			int wait = 1000;
			while(keepRunning) {
				Thread.sleep(wait);
				
				for( Door d : doors ) d.saveCurrentReading();
				for( Door d : doors ) d.updateStatistics();
				for( Person p : people ) p.updateStatistics();
				for( Equipment e : equipment ) e.updateStatistics();
			}
		}
		catch( Exception e ) {
			throw new RuntimeException("Problem with daemon: ", e);
		}
	}
}
