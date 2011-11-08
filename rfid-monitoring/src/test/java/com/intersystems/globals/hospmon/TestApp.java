package com.intersystems.globals.hospmon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.intersystems.globals.hospmon.persistence.Context;
import com.intersystems.globals.hospmon.webapp.HospitalMonitor;

/**
 * Basic Test of the Application
 * 
 * @author tspencer
 */
public class TestApp {

	@Before
	public void setup() throws Exception {
		HospitalMonitor.setup();
	}
	
	@Test
	public void inject() throws Exception {
		MockCollectors.getInstance().start(1000);
		Thread.sleep(5000);
		MockCollectors.getInstance().stop();
	}
	
	@After
	public void teardown() throws Exception {
		Context.getContext().terminate();
	}
}
