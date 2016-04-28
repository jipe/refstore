package metastore.controllers;

import java.io.IOException;

import javax.servlet.ServletException;

import metastore.servlet_extensions.RequestContext;

public class DashboardController extends MetastoreController {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(RequestContext context) throws ServletException, IOException {
    	context.showView("dashboard/index.jsp");
    }
}
