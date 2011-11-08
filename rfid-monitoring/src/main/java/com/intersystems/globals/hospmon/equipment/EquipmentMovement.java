package com.intersystems.globals.hospmon.equipment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.intersystems.globals.hospmon.base.LinkWriter;
import com.intersystems.globals.hospmon.base.Reading;
import com.intersystems.globals.hospmon.doors.DoorType;
import com.intersystems.globals.hospmon.people.PersonType;

public class EquipmentMovement extends Reading<EquipmentType> {
	
	/** The ID of the door the person went through */
	private final String door;
	/** The ID of the people going through the door at same time */
	private final List<String> people;

	public EquipmentMovement(EquipmentType personType, String equipId, Date time, String door) {
		super(personType, equipId, time);
		this.door = door;
		this.people = new ArrayList<String>();
	}
	
	public String getDoor() {
		return door;
	}
	
	public List<String> getPeople() {
		return people;
	}
	
	public void addPerson(String person) {
		people.add(person);
	}
	
	@Override
	protected void addReadingValueNames(List<String> names) {
		names.add("Door");
		names.add("People");
	}
	
	@Override
	protected void addReadingValues(LinkWriter linkWriter,
			Map<String, String> values) {
		
		values.put("Door", linkWriter.linkAsset(DoorType.ASSET_CLASS, door, door));
		
		StringBuilder buf = new StringBuilder();
		for( String person : people ) {
			if( buf.length() > 0 ) buf.append(", ");
			buf.append(linkWriter.linkAsset(PersonType.ASSET_CLASS, person, person));
		}
		
		values.put("People", buf.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("EquipmentMovement [");
		buf.append("type=").append(getAssetType().getType());
		buf.append(", id=").append(getAssetId());
		buf.append(", door=").append(door);
		buf.append(", time=").append(getTime());
		buf.append("]");
		return buf.toString();
	}
}
