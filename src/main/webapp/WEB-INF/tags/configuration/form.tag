<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="configuration" description="Configuration object" required="true" type="refstore.configuration.Configuration" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<form action="${contextPath}/configuration" method="POST">
	<div class="form-group">
		<c:forEach items="${configuration.entrySet()}" var="entry">
			<label for="${entry.key}">${entry.key}</label>
			<input type="text" name="${entry.key}" value="${entry.value}" class="form-control">
		</c:forEach>
	</div>
	<button type="submit" class="btn btn-primary">Save</button>
</form>