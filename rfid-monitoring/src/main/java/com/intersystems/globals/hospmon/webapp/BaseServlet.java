package com.intersystems.globals.hospmon.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * This helper servlet provides a lot of the basic html output
 * as we are not using JSPs.
 * 
 * @author tspencer
 */
public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected int getValue(String val) {
		if( val == null || val.length() == 0 ) return 0;
		
		try {
			return Integer.parseInt(val);
		}
		catch( Exception e ) {
			return 0;
		}
	}
	
	protected String getStringValue(String val) {
		if( val == null || val.length() == 0 ) return null;
		return val;
	}

	protected void startHtml(HttpServletResponse response, String title, int refresh) throws IOException {
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

		response.getWriter().append("<html>");
		response.getWriter().append("<head><title>");
		response.getWriter().append(title);
		response.getWriter().append("</title>");
		response.getWriter().flush();
		
		response.getWriter().append("<style>\n");
		response.getWriter().append("body { font-family: Verdana; font-size: 0.9em; }\n");
		response.getWriter().append("span.text label { display: inline-block; width: 8em; text-align: right; padding-right: 5px; }\n");
		response.getWriter().append("span.check label { text-align: left; padding-left: 5px; }\n");
		response.getWriter().append("div.submit { margin-top: 10px; padding-left: 8em; }\n");
		response.getWriter().append("div.stat { white-space: nowrap; }\n");
		response.getWriter().append("span.value { display: inline-block; width: 12em; text-align: right; font-weight: bold; }\n");
		response.getWriter().append("span.asset { display: 12em; text-align: left; font-weight: bold; }\n");
		response.getWriter().append("span.heading {	display: inline; width: auto; font-size: 0.8em; font-weight: normal; font-style: italic; color: grey; padding-right: 3px; }\n");
		response.getWriter().append("div.even { }\n");
		response.getWriter().append("div.odd { background-color: #dedede; }\n");
		response.getWriter().append("div.reading span { vertical-align: top; }\n");
		response.getWriter().append("span.asset { width: 250px; display: inline-block; }\n");
		response.getWriter().append("span.TotalAssets { width: 125px; display: inline-block; }\n");
		response.getWriter().append("span.AverageAssets { width: 125px; display: inline-block; }\n");
		response.getWriter().append("span.AverageReadings { width: 125px; display: inline-block; }\n");
		response.getWriter().append("span.Periods { width: 125px; display: inline-block; }\n");
		response.getWriter().append("span.TotalPeriods { width: 125px; display: inline-block; }\n");
		
		response.getWriter().append("span.Time { width: 200px; }");
		response.getWriter().append("span.Door { width: 400px; display: inline-block;  }");
		response.getWriter().append("span.People { width: 400px; display: inline-block;  }");
		response.getWriter().append("span.Equipment { width: 400px; display: inline-block; }");
		response.getWriter().append("</style>");
		response.getWriter().flush();
		
		response.getWriter().append("\n<script type=\"text/JavaScript\">\n");
		response.getWriter().append("<!--\n");
		if( refresh > 0 ) {
			response.getWriter().append("var refreshTimer=setTimeout('timedRefresh()', ").append(Integer.toString(refresh*1000)).append(");\n");
		}
		else {
			response.getWriter().append("var refreshTimer=null;\n");
		}

		response.getWriter().append("function timedRefresh() {\n");
		response.getWriter().append("	location.reload(true);\n");
		response.getWriter().append("}\n");

		response.getWriter().append("function clearAutoRefresh() {\n");
		response.getWriter().append("	if( refreshTimer != null ) clearTimeout(refreshTimer);\n");
		response.getWriter().append("}\n");
		response.getWriter().append("//   -->\n");
		response.getWriter().append("</script>\n");
		
		response.getWriter().append("</head><body>");
		response.getWriter().flush();
	}
	
	protected void endHtml(PrintWriter writer) {
		writer.append("</body></hmtl>");
		writer.flush();
	}
	
	protected void writeTextInput(PrintWriter writer, String label, String name, String value) {
		writer.append("<span class=\"text\">");
		writer.append("<label for=\"").append(name).append("\">").append(label).append("</label>");
		writer.append("<input type=\"text\" onfocus=\"clearAutoRefresh()\" name=\"").append(name).append("\" ");
		if( value != null ) writer.append("value=\"").append(value).append("\"></input>");
		else writer.append("value=\"\"></input>");
		writer.append("</span>");
	}
	
	protected void writeHiddenInput(PrintWriter writer, String label, String name, String value) {
		writer.append("<input type=\"hidden\" name=\"").append(name).append("\" ");
		if( value != null ) writer.append("value=\"").append(value).append("\"></input>");
		else writer.append("value=\"\"></input>");
	}
	
	protected void writeSelectInput(PrintWriter writer, String label, String name, String value, String allKey, String all, Map<String, String> values) {
		writer.append("<span class=\"select\">");
		writer.append("<label for=\"").append(name).append("\">").append(label).append("</label>");
		writer.append("<select type=\"text\" onfocus=\"clearAutoRefresh()\" name=\"").append(name).append("\">");
		
		if( allKey != null ) {
			writer.append("<option value=\"").append(allKey).append("\">").append(all).append("</option>");
		}
		
		for( String k : values.keySet() ) {
			writer.append("<option value=\"").append(k).append("\"");
			if( k.equals(value) ) writer.append(" selected=\"selected\"");
			writer.append(">").append(values.get(k)).append("</option>");
		}
		
		writer.append("</select>");
		writer.append("</span>");
	}
	
	protected void writeCheck(PrintWriter writer, String label, String name, boolean checked) {
		writer.append("<span class=\"check\">");
		writer.append("<input type=\"checkbox\" onfocus=\"clearAutoRefresh()\" name=\"").append(name).append("\" ");
		if( checked ) writer.append("checked=\"checked\" ");
		writer.append("value=\"true\"></input>");
		writer.append("<label for=\"").append(name).append("\">").append(label).append("</label>");
		writer.append("</span>");
	}
	
	protected void writeSubmit(PrintWriter writer) {
		writer.append("<br />");
		writer.append("<div class=\"submit\"><input type=\"submit\" value=\"Submit\"></input></div>");
	}
}
