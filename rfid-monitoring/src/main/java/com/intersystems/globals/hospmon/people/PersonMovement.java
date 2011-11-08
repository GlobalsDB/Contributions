package com.intersystems.globals.hospmon.people;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.intersystems.globals.hospmon.base.LinkWriter;
import com.intersystems.globals.hospmon.base.Reading;
import com.intersystems.globals.hospmon.doors.DoorType;
import com.intersystems.globals.hospmon.equipment.EquipmentType;

public class PersonMovement extends Reading<PersonType> {
	
	/** The ID of the door the person went through */
	private final String door;
	/** The ID of the equipment going through the door */
	private final List<String> equipment;

	public PersonMovement(PersonType personType, String personId, Date time, String door) {
		super(personType, personId, time);
		this.door = door;
		this.equipment = new ArrayList<String>();
	}
	
	public String getDoor() {
		return door;
	}
	
	public List<String> getEquipment() {
		return equipment;
	}
	
	public void addEquipment(String equipment) {
		this.equipment.add(equipment);
	}
	
	@Override
	protected void addReadingValueNames(List<String> names) {
		names.add("Door");
		names.add("Equipment");
	}
	
	@Override
	protected void addReadingValues(LinkWriter linkWriter,
			Map<String, String> values) {
		
		values.put("Door", linkWriter.linkAsset(DoorType.ASSET_CLASS, door, door));
		
		StringBuilder buf = new StringBuilder();
		for( String equip : equipment ) {
			if( buf.length() > 0 ) buf.append(", ");
			buf.append(linkWriter.linkAsset(EquipmentType.ASSET_CLASS, equip, equip));
		}
		
		values.put("Equipment", buf.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("PersonMovement [");
		buf.append("type=").append(getAssetType().getType());
		buf.append(", id=").append(getAssetId());
		buf.append(", door=").append(door);
		buf.append(", time=").append(getTime());
		buf.append("]");
		return buf.toString();
	}
}
