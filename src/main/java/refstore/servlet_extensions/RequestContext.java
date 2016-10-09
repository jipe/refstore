package refstore.servlet_extensions;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

	public HttpServletRequest getRequest() {
		return request;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	public void showView(String name) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/" + name).forward(request, response);
	}

	public void addRequestParameter(String name, Object value) {
		request.setAttribute(name, value);
	}
	
	public String getParameter(String name) {
		return request.getParameter(name);
	}
	
	public String getContextPath() {
		return request.getContextPath();
	}
	
	public Map<String, String> getParameters() {
		Map<String, String> result = new HashMap<>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			result.put(name, request.getParameter(name));
		}
		return result;
	}
	
	public void sendRedirect(String location) throws IOException {
		response.sendRedirect(location);
	}
}
