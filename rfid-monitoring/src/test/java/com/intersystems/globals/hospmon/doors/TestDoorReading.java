package com.intersystems.globals.hospmon.doors;

import static org.mockito.Mockito.times;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.intersys.globals.Connection;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.intersystems.globals.hospmon.persistence.Context;

/**
 * Simple test of the door reading
 * 
 * @author tspencer
 */
public class TestDoorReading {

	/** The mock connection set in the tests */
	private Connection connection;
	/** The door type we are using */
	private DoorType mainDoorType;
	/** The door itself */
	private Door doorOne;
	
	@Before
	public void setup() {
		connection = Mockito.mock(Connection.class);
		Context context = Context.getContext();
		context.init(connection);
		
		mainDoorType = new DoorType("MainDoor", false, 100, 1, 10);
	}
	
	@Test
	public void basic() {
		NodeReference node = Mockito.mock(NodeReference.class);
		ValueList lst = Mockito.mock(ValueList.class);
		Mockito.when(connection.createNodeReference(mainDoorType.getType())).thenReturn(node);
		Mockito.when(connection.createList()).thenReturn(lst);
		
		doorOne = new Door(mainDoorType, "Door1");
		
		doorOne.addToReading(null, null);
		doorOne.saveCurrentReading();
		
		Mockito.verify(node, times(2)).appendSubscript(doorOne.getId());
		Mockito.verify(node).appendSubscript("readings");
		Mockito.verify(node).appendSubscript(Mockito.anyLong());
		//Mockito.verify(lst).append("X123");
		//Mockito.verify(node).set(lst);
		
		//Assert.assertEquals(1, doorOne.getTotalAssetsPeriod());
	}
}
