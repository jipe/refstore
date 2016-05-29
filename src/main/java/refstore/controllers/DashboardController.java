package refstore.controllers;

import java.io.IOException;

import javax.servlet.ServletException;

import refstore.servlet_extensions.RequestContext;

public class DashboardController extends RefStoreController {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(RequestContext context) throws ServletException, IOException {
		context.showView("dashboard/index.jsp");
	}
}
