<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="title" description="The page title" required="true" type="java.lang.String" rtexprvalue="true" %>
<!DOCTYPE html>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/refstore.css">
    <title>${title}</title>
  </head>
  <body>
    <h1>${title}</h1>
    <jsp:doBody></jsp:doBody>
  </body>
</html>