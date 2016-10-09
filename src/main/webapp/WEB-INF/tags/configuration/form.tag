<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="configuration" description="Configuration object" required="true" type="refstore.configuration.Configuration" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<form action="${contextPath}/configuration" method="POST">
	<c:forEach items="${configuration.orderedKeys()}" var="key">
		<div class="form-group">
			<label for="${key}">${key}</label>
			<div class="input-group">
				<input type="text" name="${key}" value="${configuration.get(key)}" class="form-control">
				<span class="input-group-btn">
					<a href="${contextPath}/configuration/reset?key=${key}" class="btn btn-default">Set to default</a>
				</span>
			</div>
		</div>
	</c:forEach>
	<button type="submit" class="btn btn-primary">Save</button>
</form>