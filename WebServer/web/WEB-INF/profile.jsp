<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <jsp:include page="header.jsp"/>
    <title>Profile</title>
</head>


<body>

<h1>Profile</h1>
<h2>Turn a user into Editor</h2>
<s:form action="turnIntoEditorAction" method="get">
    <s:text name="Email:" />
    <s:textfield name="regular" />
    <s:submit />
</s:form>

</body>
</html>
