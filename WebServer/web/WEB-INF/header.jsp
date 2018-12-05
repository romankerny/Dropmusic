<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <style><%@include file="/WEB-INF/css/header.css"%></style>
    <script><%@include file="/WEB-INF/js/notification.js"%></script>
    <script>setEmail("${session.email}")</script>
</head>
<body>

<ul>
    <li><a href="dropmusic" class="active">Home</a></li>
    <li><a href="artists.jsp">Artists</a></li>
    <li><a href="#contact">Albuns</a></li>
    <li><a href="profile">Profile</a></li>
    <li id="search-bar">
        <s:form action="searchAll" method="GET">
            <s:textfield name="inputObject.keyword" />
        </s:form>
    </li>

    </li>
</ul>

<div>
    <div id="container"><div id="notifications"></div></div>
</div>

</body>
</html>
