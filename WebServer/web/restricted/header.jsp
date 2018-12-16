<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <style><%@include file="../css/header.css"%></style>
    <script><%@include file="../js/notification.js"%></script>
    <script>setEmail("${session.email}")</script>
</head>
<body>

<ul id="header">
    <li class="head"><a href="dropmusic" class="active">Home</a></li>
    <li class="head"><a href="manage">Manage</a></li>
    <li class="head"><a href="profile">Profile</a></li>
    <li class="head"><a href="logout">Logout</a></li>
    <li class="head" id="search-bar">
        <s:form action="searchAll" method="GET">
            <s:textfield name="keyword" />
        </s:form>
    </li>

    </li>
</ul>

<div>
    <div id="container"><div id="notifications"></div></div>
</div>

</body>
</html>
