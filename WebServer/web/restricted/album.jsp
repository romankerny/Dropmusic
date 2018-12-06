<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 06/12/2018
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="header.jsp"/>
    <title>Album | DropMusic</title>
</head>
<body>

<c:choose>
    <c:when test="${results == null}">
        A problem occurred during the search!
    </c:when>
    <c:when test="${results.isEmpty()}">
        No results found!
    </c:when>
    <c:otherwise>
        <br />
        <c:forEach items="${results}" var="item">
            <h1>
                <c:out value="${item.title}" />
            </h1>
            <div>
                <b>Description:</b> <c:out value="${item.description}" /> <br/>
                <b>Genre:</b> <c:out value="${item.genre}" /> <br/>
                <b>Launch Date:</b> <c:out value="${item.launchDate}" /> <br/>
                <b>Editor Label:</b> <c:out value="${item.editorLabel}" /> <br/>
                <b>Averate Rating:</b> <c:out value="${item.avgRating}" /> <br/>

            </div>
            <div>
                <h2>
                    Write a review:
                </h2>
            </div>
            <br />
        </c:forEach>
    </c:otherwise>
</c:choose>

</body>
</html>
