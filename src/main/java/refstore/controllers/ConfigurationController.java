package refstore.controllers;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import refstore.RefStore;
import refstore.configuration.Configuration;
import refstore.servlet_extensions.RequestContext;

public class ConfigurationController extends RefStoreController {

	private static final long serialVersionUID = 1L;
	
	private Configuration configuration;
	
	@Override
	public void init() throws ServletException {
		super.init();
		configuration = ((RefStore) getServletContext().getAttribute("refStore")).getConfiguration(); 
	}

	@Override
	protected void doGet(RequestContext context) throws ServletException, IOException {
		context.addRequestParameter("configuration", configuration);
		context.showView("configuration/index.jsp");
	}
	
	@Override
	protected void doPost(RequestContext context) throws ServletException, IOException {
		for (Entry<String, String> entry : context.getParameters().entrySet()) {
			configuration.put(entry.getKey(), entry.getValue());
		}
		configuration.save();
		context.sendRedirect(String.format("%s/configuration", context.getContextPath()));
	}
}
