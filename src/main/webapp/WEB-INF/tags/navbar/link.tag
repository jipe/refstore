<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="href" description="Link target" required="true" type="java.lang.String" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
	<c:when test="${requestUri eq href}">
		<li class="active"><a href="#"><jsp:doBody></jsp:doBody></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${href}"><jsp:doBody></jsp:doBody></a></li>
	</c:otherwise>
</c:choose>
