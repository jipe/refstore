<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="conf" tagdir="/WEB-INF/tags/configuration" %>
<l:standard title="Configuration">
	<conf:form configuration="${configuration}"></conf:form>
</l:standard>