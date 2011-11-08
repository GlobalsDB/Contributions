package com.intersystems.globals.hospmon.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intersystems.globals.hospmon.Injector;

public class InjectorServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	private Injector injector = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String refresh = req.getParameter("refresh");
		String rate = req.getParameter("rate");
	
		int targetRate = getValue(rate);
		int refreshRate = getValue(refresh);
		
		startHtml(resp, "Injector (Refresh " + refreshRate + " Seconds)", refreshRate);
		initialForm(resp.getWriter(), rate, refresh);
		if( targetRate > 0 ) inject(resp.getWriter(), targetRate);
		endHtml(resp.getWriter());
	}
	
	private void inject(PrintWriter writer, int rate) {
		if( injector == null ) injector = new Injector(rate, 0);
		else injector.setRate(rate);
		
		long time = System.currentTimeMillis();
		int written = injector.injectOnce();
		writer.append("<p>Written ").append(Integer.toString(written)).append(" in ").append(Long.toString(System.currentTimeMillis() - time)).append("ms</p>");
	}
	
	/**
	 * Displays the header to determine what to display
	 */
	public void initialForm(PrintWriter writer, String rate, String refresh) {
		writer.append("\n");
		writer.append("<h3>Injector</h3>");
		
		writer.append("<p>The injector will on every request generate readings against people at the rate specified. ");
		writer.append("The injector will only generate 1 request per person at a time so will not acheive the rate. ");
		writer.append("However, as the door the person moves through also has a reading against it the rate returned will likely be higher. ");
		writer.append("The injector will also invite any asset to save its averages if applicable - assets do not save their averages all the time.");
		writer.append("The page will auto-refresh if you set a value (in seconds).</p>");
		
		writer.append("<form action=\"injector\" method=\"get\">");
		writeTextInput(writer, "Rate", "rate", rate);
		writeTextInput(writer, "Refresh", "refresh", refresh);
		writeSubmit(writer);
		writer.append("</form>");
		writer.append("<hr />\n");
		writer.flush();
	}
	
	
}
