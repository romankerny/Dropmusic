<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 28/11/2018
  Time: 20:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
    <title>Search results</title>
</head>
<body>

<div id="main">
    <c:choose>
        <c:when test="${results == null}">
            A problem occurred during the search!
        </c:when>
        <c:when test="${results.isEmpty()}">
            No results found!
        </c:when>
        <c:otherwise>
            Found ${results.size()} products!
            <br />
            <c:forEach items="${results}" var="item">
                <div>
                    Title: <c:out value="${item.title}" /> <br />
                </div>
                <br />
            </c:forEach>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>