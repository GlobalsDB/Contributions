package com.intersystems.globals.hospmon.webapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersystems.globals.hospmon.Generator;
import com.intersystems.globals.hospmon.doors.DoorType;
import com.intersystems.globals.hospmon.equipment.EquipmentType;
import com.intersystems.globals.hospmon.people.PersonType;
import com.intersystems.globals.hospmon.persistence.Context;

public class HospitalMonitor {
	private static final Logger logger = LoggerFactory.getLogger(HospitalMonitor.class);

	public static void main(String[] args) throws Exception
    {
		setup();
		
		Server server = new Server(4080);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        context.addServlet(new ServletHolder(new HospMonServlet()), "/hospmon");
        context.addServlet(new ServletHolder(new StatisticsServlet()), "/statistics");
        context.addServlet(new ServletHolder(new ReadingsServlet()), "/readings");
        
        server.start();
        server.join();
        
        Context.getContext().terminate();
    }
	
	/**
	 * Helper to setup types and assets at start
	 */
	public static void setup() {
		Connection connection = ConnectionContext.getConnection();
		if( !connection.isConnected() ) connection.connect("", "", "");
		Context.getContext().init(connection);
		
		Generator.deleteAllAndGenerateTypes();
		
		// Generate doors (approx 500)
		logger.info("Generating Doors ...");
		Generator.generateDoorPack("Building 1", 1);
		Generator.generateDoorPack("Building 1", 2);
		Generator.generateDoorPack("Building 1", 3);
		Generator.generateDoorPack("Building 1", 4);
		Generator.generateDoorPack("Building 1", 5);
		Generator.generateDoorPack("Building 2", 1);
		Generator.generateDoorPack("Building 2", 2);
		Generator.generateDoorPack("Building 2", 3);
		Generator.generateDoorPack("Building 3", 1);
		Generator.generateDoorPack("Building 3", 2);
		
		// Generate people (approx 4000)
		int doorPacks = 100;
		logger.info("Generating {} People Packs ...", doorPacks);
		for( int i = 0 ; i < doorPacks ; i++ ) {
			Generator.generatePeoplePack(i);
		}
		
		// Generate Equipment (approx 40000)
		int equipmentPacks = 600;
		logger.info("Generating {} Equipment Packs ...", equipmentPacks);
		for( int i = 0 ; i < equipmentPacks ; i++ ) {
			Generator.generateEquipment(i);
		}
		
		DoorType.refreshDoorTypes();
		PersonType.refreshPersonTypes();
		EquipmentType.refreshEquipmentTypes();
	}
}
