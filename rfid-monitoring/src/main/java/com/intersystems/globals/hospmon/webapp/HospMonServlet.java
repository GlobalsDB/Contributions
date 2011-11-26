package com.intersystems.globals.hospmon.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intersystems.globals.hospmon.CollectionStats;
import com.intersystems.globals.hospmon.MockCollectors;

public class HospMonServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		MockCollectors mock = MockCollectors.getInstance();
		
		int refreshRate = getValue(req.getParameter("refreshRate"));
		int targetRate = getValue(req.getParameter("targetRate"));
		
		startHtml(resp, "Injector (Refresh " + refreshRate + " Seconds)", (mock.isRunning() || req.getParameter("start") != null) ? refreshRate : 0);
		
		// We are to start if the start button is submitted, otherwise
		if( req.getParameter("start") != null && !mock.isRunning() ) {
			mock.start(targetRate);
			
			writeStopForm(resp.getWriter(), targetRate, refreshRate, 0, 0);
		}
		else if( req.getParameter("stop") != null && mock.isRunning() ) {
			mock.stop();
			
			writeStartForm(resp.getWriter(), targetRate);
		}
		else if( mock.isRunning() ) {
			CollectionStats stats = mock.getCollectionStats();
			writeStopForm(resp.getWriter(), targetRate, refreshRate, stats.getReadingsTaken(), stats.getTotalSaveTime());
		}
		else {
			writeStartForm(resp.getWriter(), targetRate);
		}
		
		endHtml(resp.getWriter());
	}

	public void writeHeading(PrintWriter writer) {
		writer.append("<h3>Hospital Monitor</h3>");
		writer.append("<p>This page a form allows you to start and stop the ");
		writer.append("Hospital Monitor Globals sample application. <br />");
		writer.append("Simply use the form below to determine your overall ");
		writer.append("target rate. The application will then generate an ");
		writer.append("appropriate number of hospital doors, people and equipment ");
		writer.append("and then generate RFID recordings of the people and ");
		writer.append("equipment through the doors.</p><p>You can use the ");
		writer.append("<a href='/statistics'>statistic</a> page to view the ");
		writer.append("statistics of the doors, people and equipment and even ");
		writer.append("go and drill down on the actual readings.</p>");
		writer.append("<p>All the time the generator is running the info is ");
		writer.append("stored in the Globals DB and the readings are taken ");
		writer.append("off the DB.</p>");
		writer.append("<p>As this is a sample app, each time the generator ");
		writer.append("starts it deletes all data.</p>");
		writer.flush();
	}
	
	public void writeStartForm(PrintWriter writer, int currentRate) {
		writer.append("<h4>Injector</h4>");
		writer.append("<p>Fill in your target refresh rate and hit start. ");
		writer.append("The sample app will create a number of MockCollectors ");
		writer.append("that will generate people and equipment moving through ");
		writer.append("doors. All these readings are recorded and then the ");
		writer.append("assets will save statistics periodically. Use the ");
		writer.append("<a href='/statistics'>Statistics</a> page to view these ");
		writer.append("statistics and the readings.</p>");
		
		writer.append("<form action=\"hospmon\" method=\"get\">");
		writeTextInput(writer, "Rate", "targetRate", Integer.toString(currentRate));
		writeTextInput(writer, "Refresh", "refreshRate", "0");
		writer.append("<br />");
		writer.append("<div class=\"submit\"><input type=\"submit\" name=\"start\" value=\"Start\"></input></div>");
		writer.append("</form>");
		writer.append("<hr />\n");
		writer.flush();
	}
		
	public void writeStopForm(PrintWriter writer, int targetRate, int refreshRate, long actualRate, long saveTime) {
		writer.append("<h4>Injector</h4>");
		writer.append("<p>The rate being hit right now is approx: ");
		writer.append(Long.toString(actualRate));
		writer.append("</p>");
		writer.append("<p>The time to save those records (approx) is: ");
		writer.append(Long.toString(saveTime));
		writer.append("&nbsp;(does not include saving statistics)</p>");
		writer.append("<p>Press 'stop' to stop the generators.</p>");
		writer.append("<form action=\"hospmon\" method=\"get\">");
		writeHiddenInput(writer, "Rate", "targetRate", Integer.toString(targetRate));
		writeTextInput(writer, "Refresh", "refreshRate", Integer.toString(refreshRate));
		writer.append("<div class=\"submit\"><input type=\"submit\" name=\"refresh\" value=\"Refresh\"></input></div>");
		writer.append("<div class=\"submit\"><input type=\"submit\" name=\"stop\" value=\"Stop\"></input></div>");
		writer.append("</form>");
		writer.append("<hr />\n");
		writer.flush();
	}
}
