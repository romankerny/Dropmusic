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
    <c:when test="${result == null}">
        No result found!
    </c:when>
    <c:otherwise>
        <h1><c:out value="${result.name}" /> <br /></h1>
        <div>
            <b>Description:</b> <c:out value="${result.details}" /> <br/>
            <c:choose>
                <c:when test="${result.albums.isEmpty()}">
                    No discography in database!
                </c:when>
                <c:otherwise>
                    <h2>Discography</h2>
                    <c:forEach items="${result.albums}" var="album">

                        <s:url value="albumSearch.action" method="execute" var="urlTag">
                            <s:param name="inputObject.artist">${result.name}</s:param>
                            <s:param name="inputObject.title">${album.title}</s:param>
                        </s:url>

                        [<c:out value="${album.launchDate}"/>] <s:a href="%{urlTag}">${album.title}</s:a>
                        <br/>
                    </c:forEach>
                </c:otherwise>
            </c:choose>

        </div>
        <br />
    </c:otherwise>
</c:choose>
</body>
</html>
