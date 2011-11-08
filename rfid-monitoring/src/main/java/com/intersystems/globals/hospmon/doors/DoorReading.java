package com.intersystems.globals.hospmon.doors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.intersystems.globals.hospmon.base.LinkWriter;
import com.intersystems.globals.hospmon.base.Reading;
import com.intersystems.globals.hospmon.equipment.EquipmentType;
import com.intersystems.globals.hospmon.people.PersonType;

public class DoorReading extends Reading<DoorType> {
	
	/** The people who have gone through in this reading */
	public final List<String> people;
	/** The equipment that went through in this reading */
	public final List<String> equipment;
	
	public DoorReading(DoorType assetType, String asset, Date time) {
		super(assetType, asset, time);
		this.people = new ArrayList<String>();
		this.equipment = new ArrayList<String>();
	}
	
	public List<String> getPeople() {
		return people;
	}
	
	public List<String> getEquipment() {
		return equipment;
	}
	
	public void addPerson(String person) {
		this.people.add(person);
	}
	
	public void addEquipment(String equipment) {
		this.equipment.add(equipment);
	}
	
	@Override
	protected void addReadingValueNames(List<String> names) {
		names.add("People");
		names.add("Equipment");
	}
	
	@Override
	protected void addReadingValues(LinkWriter linkWriter,
			Map<String, String> values) {
		StringBuilder buf = new StringBuilder();
		for( String person : people ) {
			if( buf.length() > 0 ) buf.append(", ");
			buf.append(linkWriter.linkAsset(PersonType.ASSET_CLASS, person, person));
			buf.append("<wbr />");
		}
		values.put("People", buf.toString());
		
		buf.setLength(0);
		for( String equip : equipment ) {
			if( buf.length() > 0 ) buf.append(", ");
			buf.append(linkWriter.linkAsset(EquipmentType.ASSET_CLASS, equip, equip));
			buf.append("<wbr />");
		}
		values.put("Equipment", buf.toString());
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("DoorReading [");
		buf.append("type=").append(getAssetType().getType());
		buf.append(", id=").append(getAssetId());
		buf.append(", people=").append(people);
		buf.append(", equip=").append(equipment);
		buf.append(", time=").append(getTime());
		buf.append("]");
		return buf.toString();
	}
	
}
