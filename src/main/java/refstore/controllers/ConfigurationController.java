package refstore.controllers;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import refstore.configuration.Configuration;
import refstore.servlet_extensions.RequestContext;

public class ConfigurationController extends RefStoreController {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(RequestContext context) throws ServletException, IOException {
		Configuration configuration = services.getConfiguration();
		context.addRequestParameter("configuration", configuration);
		String path = context.getRequest().getPathInfo();
		if ("/reset".equals(path)) {
			String key = context.getParameter("key");
			configuration.setToDefault(key);
			services.getConfigurationStore().save(configuration);
			context.sendRedirect(String.format("%s/configuration", context.getContextPath()));
		} else {
			context.showView("configuration/index.jsp");
		}
	}
	
	@Override
	protected void doPost(RequestContext context) throws ServletException, IOException {
		Configuration configuration = services.getConfiguration();
		for (Entry<String, String> entry : context.getParameters().entrySet()) {
			configuration.put(entry.getKey(), entry.getValue());
		}
		services.getConfigurationStore().save(configuration);
		context.sendRedirect(String.format("%s/configuration", context.getContextPath()));
	}
}
