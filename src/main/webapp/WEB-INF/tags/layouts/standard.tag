<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="title" description="The page title" required="true"
	type="java.lang.String" rtexprvalue="true"%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/refstore.css">
		<title>${refStore.configuration.get('application.title')}</title>
	</head>
	<body>
		<nav class="navbar navbar-default">
			<div class="container">
				<div class="navbar-header">
					<a href="${contextPath}" class="navbar-brand">${refStore.configuration.get('application.title')}</a>
				</div>
				<ul class="nav navbar-nav">
					<li><a href="${contextPath}/overview">Overview</a></li>
					<li><a href="${contextPath}/pipelines">Pipelines</a></li>
					<li><a href="${contextPath}/harvesters">Harvesters</a></li>
					<li><a href="${contextPath}/extractors">Extractors</a></li>
					<li><a href="${contextPath}/transformers">Transformers</a></li>
					<li class="active"><a href="#">Configuration</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="${contextPath}/login">Log in</a></li>
				</ul>
			</div>
		</nav>
		<div class="container">
			<jsp:doBody></jsp:doBody>
		</div>
	</body>
</html>