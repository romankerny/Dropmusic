<%--
  Created by IntelliJ IDEA.
  User: diogo
  Date: 03-12-2018
  Time: 16:59
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <style><%@include file="/WEB-INF/css/header.css"%></style>
    <script><%@include file="/WEB-INF/js/notification.js"%></script>
    <script>setEmail("${session.email}")</script>
</head>
<body>

<ul>
    <li><a href="#home" class="active">Home</a></li>
    <li><a href="artists.jsp">Artists</a></li>
    <li><a href="#contact">Albuns</a></li>
    <li><a href="#users">Profile</a></li>
    <li>
        <s:form action="search" method="GET">
            <s:textfield name="keyword" />
        </s:form>
    </li>
</ul>

<div>
    <div id="container"><div id="notifications"></div></div>
</div>

</body>
</html>
