package com.intersystems.globals.hospmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.intersystems.globals.hospmon.doors.Door;
import com.intersystems.globals.hospmon.doors.DoorType;
import com.intersystems.globals.hospmon.equipment.Equipment;
import com.intersystems.globals.hospmon.equipment.EquipmentType;
import com.intersystems.globals.hospmon.people.Person;
import com.intersystems.globals.hospmon.people.PersonType;

/**
 * Contains various methods to generate
 * 
 * @author tspencer
 */
public class Generator {
	
	/**
	 * Deletes all data and creates our sample asset types
	 */
	public static void deleteAllAndGenerateTypes() {
		DoorType.removeAll(true);
		PersonType.removeAll(true);
		EquipmentType.removeAll(true);
		
		// Doors
		DoorType type = new DoorType("MainDoor", true, 2, 2, 100); type.saveAssetType();
		type = new DoorType("CorridorDoor", true, 5, 2, 20); type.saveAssetType();
		type = new DoorType("RoomDoor", true, 10, 0, 10); type.saveAssetType();
		
		// People
		PersonType personType = new PersonType("Doctor", true, 2, 0, 10); personType.saveAssetType();
		personType = new PersonType("Nurse", true, 5, 0, 10); personType.saveAssetType();
		personType = new PersonType("Assistant", true, 5, 0, 10); personType.saveAssetType();
		personType = new PersonType("InPatient", true, 5, 0, 10); personType.saveAssetType();
		
		// Equipment
		EquipmentType equipType = new EquipmentType("Bed", true, 3, 0, 10); equipType.saveAssetType();
		equipType = new EquipmentType("Pager", true, 5, 0, 50); equipType.saveAssetType();
		equipType = new EquipmentType("HeartMonitor", true, 5, 0, 5); equipType.saveAssetType();
		equipType = new EquipmentType("BloodPressureMonitor", true, 5, 0, 5); equipType.saveAssetType();
		equipType = new EquipmentType("BabyMonitor", true, 5, 0, 5); equipType.saveAssetType();
		equipType = new EquipmentType("Crutches", true, 10, 0, 3); equipType.saveAssetType();
		
		DoorType.refreshDoorTypes();
		PersonType.refreshPersonTypes();
		EquipmentType.refreshEquipmentTypes();
	}
	
	/**
	 * Helper to generate a single 'pack' of doors in
	 * a hosptal. Each pack is 2 main doors, 8 corridor
	 * doors and 32 
	 */
	public static List<Door> generateDoorPack(String building, int floor) {
		String doorNames = "ABCDEFGH";
		
		// Get the types
		DoorType mainDoor = DoorType.getDoorType("MainDoor");
		DoorType corridorDoor = DoorType.getDoorType("CorridorDoor");
		DoorType roomDoor = DoorType.getDoorType("RoomDoor");
		
		Random r = new Random(System.nanoTime());
		
		List<Door> ret = new ArrayList<Door>(88);
		int mainDoorQuantity = 8;
		int corridorsPerMainDoor = 2;
		int roomsPerCorridor = 4;
		
		for( int j = 0 ; j < mainDoorQuantity ; j++ ) {
			String baseName = building + ", " + floor;
			String mainName = baseName + ":" + doorNames.charAt(j);
			ret.add(new Door(mainDoor, mainName, 1, true));
			
			for( int k = 0 ; k < corridorsPerMainDoor ; k++ ) {
				// Give it a chance of 1 in 2, 1 in 3 or 1 in 4 of being picked when it comes up
				int chance = 2 + r.nextInt(3);
				String corridorName = mainName + ", Corridor " + (k + 1);
				ret.add(new Door(corridorDoor, corridorName, chance, true));
				
				for( int m = 0 ; m < roomsPerCorridor ; m++ ) {
					// Give it a chance of 1 in 5-1 in 10 of being picked when it comes up
					chance = 5 + r.nextInt(6);
					String roomName = mainName + ", Room " + (k+1) + "-" + (m + 1);
					ret.add(new Door(roomDoor, roomName, chance, true));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Generates a pack of people, there is 1 doctors, 10 nurses,
	 * 15 assistants and 20 patients per pack, 46 people in total
	 */
	public static List<Person> generatePeoplePack(int pack) {
		// a. Get the People Types
		PersonType doctor = PersonType.getPersonType("Doctor");
		PersonType nurse = PersonType.getPersonType("Nurse");
		PersonType assistant = PersonType.getPersonType("Assistant");
		PersonType inpatient = PersonType.getPersonType("InPatient");
		
		// b. Create the People
		Person p = null;
		List<Person> ret = new ArrayList<Person>(46);
		
		p = new Person(doctor, "Doctor " + pack, true);
		ret.add(p);
		
		// nurse
		for( int j = 0 ; j < 10 ; j++ ) {
			p = new Person(nurse, "Nurse " + ((pack*10)+(j+1)), true);
			ret.add(p);
		}
		
		// admin
		for( int j = 0 ; j < 15 ; j++ ) {
			p = new Person(assistant, "Assistant " + ((pack*15)+(j+1)), true);
			ret.add(p);
		}
		
		// patients
		for( int j = 0 ; j < 20 ; j++ ) {
			p = new Person(inpatient, "In-Patient" + ((pack*20)+(j+1)), true);
			ret.add(p);
		}
		
		return ret;
	}
	
	/**
	 * Generates an equipment pack, contains 75 items
	 */
	public static List<Equipment> generateEquipment(int pack) {
		EquipmentType bed = EquipmentType.getEquipmentType("Bed");
		EquipmentType pager = EquipmentType.getEquipmentType("Pager");
		EquipmentType heartMonitor = EquipmentType.getEquipmentType("HeartMonitor");
		EquipmentType bloodMonitor = EquipmentType.getEquipmentType("BloodPressureMonitor");
		EquipmentType babyMonitor = EquipmentType.getEquipmentType("BabyMonitor");
		EquipmentType crutches = EquipmentType.getEquipmentType("Crutches");
		
		List<Equipment> ret = new ArrayList<Equipment>(75);
		
		// Beds - 20
		int quantity = 20;
		for( int i = 0 ; i < quantity ; i++ ) {
			String name = "Bed " + (pack * quantity + i);
			ret.add(new Equipment(bed, name, true));
		}
		
		// Pagers - 15
		quantity = 15;
		for( int i = 0 ; i < quantity ; i++ ) {
			String name = "Pager " + (pack * quantity + i);
			ret.add(new Equipment(pager, name, true));
		}
		
		// HeartMonitor - 10
		quantity = 10;
		for( int i = 0 ; i < quantity ; i++ ) {
			String name = "HeartMonitor " + (pack * quantity + i);
			ret.add(new Equipment(heartMonitor, name, true));
		}
		
		// BloodPressure - 10
		quantity = 10;
		for( int i = 0 ; i < quantity ; i++ ) {
			String name = "BloodMonitor " + (pack * quantity + i);
			ret.add(new Equipment(bloodMonitor, name, true));
		}
		
		// BabyMonitor - 5
		quantity = 5;
		for( int i = 0 ; i < quantity ; i++ ) {
			String name = "BabyMonitor " + (pack * quantity + i);
			ret.add(new Equipment(babyMonitor, name, true));
		}
		
		// Crutches - 15
		quantity = 15;
		for( int i = 0 ; i < quantity ; i++ ) {
			String name = "CrutchSet " + (pack * quantity + i);
			ret.add(new Equipment(crutches, name, true));
		}
		
		return ret;
	}
}
