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
            <c:forEach items="${artistResults}" var="item">
                <div>
                    Name: <s:url value="artistSearch.action" method="execute" var="urlTag">
                                <s:param name="inputObject.keyword">${item.name}</s:param>
                            </s:url>
                    <s:a href="%{urlTag}">${item.name}</s:a>
                    <br />
                </div>
                <br />
            </c:forEach>
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
            <c:forEach items="${albumResults}" var="item">
                <div>
                Title: <s:url value="musicSearch.action" method="execute" var="urlTag">
                             <s:param name="inputObject.keyword">${item.title}</s:param>
                        </s:url>
                    <s:a href="%{urlTag}">${item.title}</s:a>
                      <br />
                </div>
                <br />
            </c:forEach>
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
            <c:forEach items="${musicResults}" var="item">
                <div>
                    Title: <s:url value="musicSearch.action" method="execute" var="urlTag">
                    <s:param name="inputObject.keyword">${item.title}</s:param>
                </s:url>
                    <s:a href="%{urlTag}">${item.title}</s:a>
                    <br />
                </div>
                <br />
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>