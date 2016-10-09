<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/navbar" %>
<%@ attribute name="title" description="The page title" required="true"
	type="java.lang.String" rtexprvalue="true"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/refstore.css">
		<script src="${contextPath}/js/jquery-3.1.1.min.js"></script>
		<script src="${contextPath}/js/bootstrap.min.js"></script>
		<script src="${contextPath}/js/refstore.js"></script>
		<title>${refStore.configuration.get('application.title')}</title>
	</head>
	<body>
		<nav class="navbar navbar-default">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
						aria-expanded="false">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a href="${contextPath}" class="navbar-brand">${refStore.configuration.get('application.title')}</a>
				</div>
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<nav:link href="${contextPath}/overview">Overview</nav:link>
						<nav:link href="${contextPath}/pipelines">Pipelines</nav:link>
						<nav:link href="${contextPath}/harvesters">Harvesters</nav:link>
						<nav:link href="${contextPath}/extractors">Extractors</nav:link>
						<nav:link href="${contextPath}/transformers">Transformers</nav:link>
						<nav:link href="${contextPath}/configuration">Configuration</nav:link>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="${contextPath}/login">Log in</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<div class="container">
			<jsp:doBody></jsp:doBody>
		</div>
	</body>
</html>