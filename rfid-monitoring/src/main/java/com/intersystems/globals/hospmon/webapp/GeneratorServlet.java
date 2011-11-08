package com.intersystems.globals.hospmon.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public class GeneratorServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
				
		String doorPacks = req.getParameter("doors");
		String peoplePacks = req.getParameter("people");
		String deleteFirst = req.getParameter("delete");
		
		startHtml(resp, "Asset Generator", 0);
		writeBody(resp.getWriter());
		if( getValue(doorPacks) > 0 || getValue(peoplePacks) > 0 ) generate(resp.getWriter(), getValue(doorPacks), getValue(peoplePacks), "true".equals(deleteFirst));
		writeForm(resp.getWriter());
		endHtml(resp.getWriter());
	}
	
	private void writeBody(PrintWriter writer) {
		writer.append("\n<h3>Asset Generator</h3>");
		writer.append("<p>This form when submitted will generate assets in the RFID Hospital Monitor App. ");
		writer.append("The units specified are in terms of packs. For each door pack you get 2 main doors, ");
		writer.append("8 corridor doors and 32 room doors. Main doors have a higher chance of being used ");		writer.append("than room doors. For each people pack you get 1 doctor, 40 nurses and 40 assistants.</p>");
	}
	
	private void generate(PrintWriter writer, int doors, int people, boolean deleteFirst) {
		/*Generator generator = new Generator(deleteFirst, doors, people);
		doors = generator.generateDoors().size();
		people = generator.generatePeople().size();
		
		if( doors > 0 ) writer.append("<p>Generated " + doors + " doors</p>");
		if( people > 0 ) writer.append("<p>Generated " + people + " people</p>");*/
	}
	
	private void writeForm(PrintWriter writer) {
		writer.append("<form action=\"generator\" method=\"get\">");
		writeTextInput(writer, "Door Packs", "doors", null);
		writeTextInput(writer, "People Packs", "people", null);
		writeCheck(writer, "Delete First", "delete", false);
		writeSubmit(writer);
		writer.append("</form>");
	}
}
