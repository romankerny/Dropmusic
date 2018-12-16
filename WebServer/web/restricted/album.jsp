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
    <script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
    <script>
        $(document).ready(function() {
            $("#addReviewButton").click(function(){
                $.ajax({
                    type: 'POST',
                    url:'addReview.action?reviewModel.rating='+ document.getElementById('rating').value
                        + '&reviewModel.critic='+document.getElementById('critic').value
                        + '&reviewModel.artist='+document.getElementById('artist').value
                        + '&reviewModel.album='+document.getElementById('album').value
                        + '&reviewModel.email='+document.getElementById('email').value
                    ,
                    dataType: 'text',
                    success: function(data){
                        document.getElementById('reviewStatus').innerHTML = data;
                    }, error: function(data) {
                        document.getElementById('reviewStatus').innerHTML = data;
                    }
                });
                return false;
            });
        });
    </script>
</head>
<body>

<c:choose>
    <c:when test="${result == null}">
        No result found
    </c:when>
    <c:otherwise>
        <h1><c:out value="${result.title}" /> by <c:out value="${result.artist}" /></h1>

        <div>
            <b>Description:</b> <c:out value="${result.description}" /> <br/>
            <b>Genre:</b> <c:out value="${result.genre}" /> <br/>
            <b>Launch Date:</b> <c:out value="${result.launchDate}" /> <br/>
            <b>Editor Label:</b> <c:out value="${result.editorLabel}" /> <br />
            <b>Averate Rating:</b> <span id="avgRating"><c:out value="${result.avgRating}" /> </span>
        </div>

        <c:choose>
            <c:when test="${result.songs.isEmpty()}">
                No tracklist in database!
            </c:when>
            <c:otherwise>
                <h2>Track listing</h2>
                <div>
                    <c:forEach items="${result.songs}" var="music">
                        <s:url value="musicSearch.action" method="execute" var="urlTag">
                            <s:param name="inputObject.artistName">${music.artistName}</s:param>
                            <s:param name="inputObject.albumTitle">${music.albumTitle}</s:param>
                            <s:param name="inputObject.title">${music.title}</s:param>
                        </s:url>

                        <c:out value="${music.track}"/> - <s:a href="%{urlTag}">${music.title} </s:a>
                        <br/>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>


        <div id="reviews">
        <c:choose>
                <c:when test="${result.reviews == null}">
                    A problem occurred during the search!
                </c:when>
                <c:when test="${result.reviews.isEmpty()}">
                    No reviews yet
                </c:when>

                <c:otherwise>
                    <h2>User reviews</h2>
                    <c:forEach items="${result.reviews}" var="review">
                        <p id="review${review.email}">
                        <b>Rating:</b> <c:out value="${review.rating}"  /> <br/>
                        <b>Email:</b> <c:out value="${review.email}" /> <br/>
                        <b>Review:</b> <c:out value="${review.critic}" /> <br/>
                        </p>
                        <p></p>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
            <s:set var="artistName">${result.artist}</s:set>
            <s:set var="albumName">${result.title}</s:set>
            <script>setAlbum('${result.title}')</script>
            <h2>
                Write a review
            </h2>
                <s:form action="addReview">
                    Rating:
                    <s:textfield type="number" name="reviewModel.rating" min="1" max="5" step="1" id="rating"/> <br/>
                    Review:
                    <s:textarea name="reviewModel.critic" id="critic"/> <br/>

                    <s:hidden name="reviewModel.artist" value="%{#artistName}" id="artist"/>
                    <s:hidden name="reviewModel.album" value="%{#albumName}" id="album"/>
                    <s:hidden name="reviewModel.email" value="%{#session.email}" id="email" />
                    <button type="button" id="addReviewButton">Review!</button>
                    <div id="reviewStatus"></div>
                </s:form>
    </c:otherwise>
</c:choose>

</body>
</html>
