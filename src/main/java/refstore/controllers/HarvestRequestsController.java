package refstore.controllers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;

import org.codehaus.jackson.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import refstore.harvesting.HarvestRequest;
import refstore.harvesting.HarvestingReceiver;
import refstore.servlet_extensions.RequestContext;

public class HarvestRequestsController extends RefStoreController {

	private static final Logger log = LoggerFactory.getLogger(HarvestRequestsController.class);
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(RequestContext context) throws ServletException, IOException {
	}
	
	@Override
	protected void doPost(RequestContext context) throws ServletException, IOException {
		services
		.getMessenger()
		.send(HarvestingReceiver.QUEUE, buildHarvestRequest(context.getParameters()));
		context.sendRedirect("/harvests");
	}
	
	private String buildHarvestRequest(Map<String, String> parameters) throws IOException {
		StringWriter writer = new StringWriter();
		JsonGenerator generator = services.getJsonFactory().createJsonGenerator(writer);
		generator.writeStartObject();
		generator.writeStringField("provider", parameters.get("provider"));
		generator.writeStringField("from", parameters.get("from"));
		generator.writeStringField("until", parameters.get("until"));
		generator.writeEndObject();
		generator.close();
		String result = writer.toString();
		log.info("Built harvest request: '{}'", result);
		return result;
	}
}
