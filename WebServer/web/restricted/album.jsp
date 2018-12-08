<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page session="true" %>
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
        <c:forEach items="${results}" var="item">
            <h1><c:out value="${item.title}" /> by <c:out value="${item.artist}" /></h1>

            <div>
                <b>Description:</b> <c:out value="${item.description}" /> <br/>
                <b>Genre:</b> <c:out value="${item.genre}" /> <br/>
                <b>Launch Date:</b> <c:out value="${item.launchDate}" /> <br/>
                <b>Editor Label:</b> <c:out value="${item.editorLabel}" /> <br/>
                <b>Averate Rating:</b> <c:out value="${item.avgRating}" /> <br/>

                <c:choose>
                    <c:when test="${item.reviews == null}">
                        A problem occurred during the search!
                    </c:when>
                    <c:when test="${item.reviews.isEmpty()}">
                        No reviews yet
                    </c:when>

                    <c:otherwise>
                        <h2>User reviews</h2>
                        <c:forEach items="${item.reviews}" var="review">
                            <b>Rating:</b> <c:out value="${review.rating}"  /> <br/>
                            <b>Email:</b> <c:out value="${review.email}" /> <br/>
                            <b>Review:</b> <c:out value="${review.critic}" /> <br/>
                            <p></p>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

            </div>
            <div>
                <s:set var="artistName">${item.artist}</s:set>
                <s:set var="albumName">${item.title}</s:set>
                <h2>
                    Write a review
                </h2>
                    <s:form action="addReview">
                        Rating:
                        <s:textfield type="number" name="reviewModel.rating" min="1" max="5" step="1" /> <br/>
                        Review:
                        <s:textarea name="reviewModel.critic" /> <br/>

                        <s:hidden name="reviewModel.artist" value="%{#artistName}"/>
                        <s:hidden name="reviewModel.album" value="%{#albumName}" />
                        <s:hidden name="reviewModel.email" value="%{#session.email}" />
                        <s:submit />
                    </s:form>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

</body>
</html>
