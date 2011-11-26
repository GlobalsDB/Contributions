package com.intersystems.globals.hospmon;

import org.junit.Before;
import org.junit.Test;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersystems.globals.hospmon.doors.DoorType;
import com.intersystems.globals.hospmon.people.PersonType;
import com.intersystems.globals.hospmon.persistence.Context;

/**
 * Tests the generator works correctly.
 * 
 * @author tspencer
 */
public class TestGenerator {
	
	@Before
	public void setup() {
		Connection connection = ConnectionContext.getConnection();
		connection.connect("", "", "");
		Context.getContext().init(connection);
		
		DoorType.removeAll(true);
		PersonType.removeAll(true);
	}

	@Test
	public void basic() {
		//Generator generator = new Generator(false, 2, 400);
		//generator.generate();
	}
	
	// @After
	public void teardown() {
		DoorType.removeAll(true);
		PersonType.removeAll(true);
		
		Context.getContext().terminate();
	}
}
