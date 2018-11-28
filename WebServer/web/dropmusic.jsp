<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: diogo
  Date: 28-11-2018
  Time: 17:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Drop Music</title>
</head>
<body>
<h1>DropMusic</h1>
<p>
    <s:form action="search" method="GET">
        <s:textfield name="keyword" />
    </s:form>
</p>
</body>
</html>
