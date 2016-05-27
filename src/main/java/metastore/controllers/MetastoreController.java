package metastore.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import metastore.servlet_extensions.RequestContext;

public abstract class MetastoreController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(new RequestContext(request, response));
	}

	protected void doGet(RequestContext context) throws ServletException, IOException {

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(new RequestContext(request, response));
	}

	protected void doPost(RequestContext context) throws ServletException, IOException {

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPut(new RequestContext(request, response));
	}

	protected void doPut(RequestContext context) throws ServletException, IOException {

	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doDelete(new RequestContext(request, response));
	}

	protected void doDelete(RequestContext context) throws ServletException, IOException {

	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHead(new RequestContext(request, response));
	}

	protected void doHead(RequestContext context) throws ServletException, IOException {

	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doOptions(new RequestContext(request, response));
	}

	protected void doOptions(RequestContext context) throws ServletException, IOException {

	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doTrace(new RequestContext(request, response));
	}

	protected void doTrace(RequestContext context) throws ServletException, IOException {

	}
}
