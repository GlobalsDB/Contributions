package com.intersystems.globals.hospmon;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.GlobalsDirectory;
import com.intersystems.globals.hospmon.persistence.Context;

public class TestInjector {
	
	private Connection connection;
	
	@Before
	public void setup() {
		connection = ConnectionContext.getConnection();
		connection.connect("USER", "SYS", "SYS");
		Context.getContext().init(connection);
	}

	@Test
	@Ignore
	public void basic() {
		GlobalsDirectory dir = connection.createGlobalsDirectory();
		String name = dir.nextGlobalName();
		int count = 10;
		while( name != null && name.length() > 0 ) {
			System.out.println("Node: " + name);
			name = dir.nextGlobalName();
			--count;
		}
		dir.close();
		
		Injector injector = new Injector(5000, 5);
		injector.inject();
	}
	
	@After
	public void teardown() {
		Context.getContext().terminate();
	}
}
