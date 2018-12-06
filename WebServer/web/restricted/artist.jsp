<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <jsp:include page="header.jsp"/>
    <title>Artist | DropMusic</title>
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
                <c:out value="${item.name}" /> <br />
            </h1>
            <div>
                <b>Description:</b> <c:out value="${item.details}" /> <br/>
            </div>
            <br />
        </c:forEach>
    </c:otherwise>
</c:choose>
</body>
</html>
