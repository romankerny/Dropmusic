<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 28/11/2018
  Time: 20:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <jsp:include page="header.jsp"/>
    <title>Search results | DropMusic</title>
</head>
<body>

<div id="main">
    <h1>Artists</h1>
    <c:choose>
        <c:when test="${artistResults == null}">
            A problem occurred during the search!
        </c:when>
        <c:when test="${artistResults.isEmpty()}">
            No results found!
        </c:when>
        <c:otherwise>
            Found ${artistResults.size()} artists!
            <br />
            <ul>
            <c:forEach items="${artistResults}" var="item">
                <li><s:url value="artistSearch.action" method="execute" var="urlTag">
                            <s:param name="inputObject.name">${item.name}</s:param>
                        </s:url>
                <s:a href="%{urlTag}">${item.name}</s:a></li>
                <br/>
            </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

    <h1>Albums</h1>

    <c:choose>
        <c:when test="${albumResults == null}">
            A problem occurred during the search!
        </c:when>
        <c:when test="${albumResults.isEmpty()}">
            No results found!
        </c:when>
        <c:otherwise>
            Found ${albumResults.size()} albums!
            <br />
            <ul>
            <c:forEach items="${albumResults}" var="item">
                <li><s:url value="albumSearch.action" method="execute" var="urlTag">
                             <s:param name="inputObject.artist">${item.artist}</s:param>
                            <s:param name="inputObject.title">${item.title}</s:param>
                    </s:url>
                    <s:a href="%{urlTag}">${item.title}</s:a> </li>
                <br />
            </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

    <h1>Songs</h1>

    <c:choose>
        <c:when test="${musicResults == null}">
            A problem occurred during the search!
        </c:when>
        <c:when test="${musicResults.isEmpty()}">
            No results found!
        </c:when>
        <c:otherwise>
            Found ${musicResults.size()} songs!
            <br />
            <ul>
            <c:forEach items="${musicResults}" var="item">
                    <li><s:url value="musicSearch.action" method="execute" var="urlTag">
                            <s:param name="inputObject.artistName">${item.artistName}</s:param>
                            <s:param name="inputObject.albumTitle">${item.albumTitle}</s:param>
                            <s:param name="inputObject.title">${item.title}</s:param>
                        </s:url>
                        <s:a href="%{urlTag}">${item.title}</s:a> </li>
                    <br />
            </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>