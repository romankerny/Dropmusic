<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>
    Artists
</h1>
<s:form action="artistSearch" method="GET">
    <s:textfield name="inputObject.keyword" />
    <s:submit label="Search!" />
</s:form>

<s:form action="albumSearch" method="GET">
    <s:textfield name="inputObject.keyword" />
    <s:submit label="Search!" />
</s:form>

<s:form action="musicSearch" method="GET">
    <s:textfield name="inputObject.keyword" />
    <s:submit label="Search!" />
</s:form>
</body>
</html>
