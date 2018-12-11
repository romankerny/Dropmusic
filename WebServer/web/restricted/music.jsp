<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Music | DropMusic</title>
    <jsp:include page="header.jsp"/>
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
        <c:forEach items="${results}" var="item">
            <h1>
                <c:out value="${item.title}" /> from <c:out value="${item.albumTitle}" /> by <c:out value="${item.artistName}" /> <br />
            </h1>
            <div>
                <b>Track:</b> <c:out value="${item.title}" /> <br/>
                <b>Title:</b> <c:out value="${item.track}" /> <br/>
                <b>Lyrics:</b> <c:out value="${item.lyrics}" /> <br/>
            </div>
            <br />
        </c:forEach>
    </c:otherwise>
</c:choose>

</body>
</html>
