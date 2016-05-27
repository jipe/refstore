package metastore.servlet_extensions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestContext {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public RequestContext(HttpServletRequest request, HttpServletResponse response) {
		this.request  = request;
		this.response = response;
	}

	public void showView(String name) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/" + name).forward(request, response);
	}

	public void addRequestParameter(String name, Object value) {
		request.setAttribute(name, value);
	}
}
