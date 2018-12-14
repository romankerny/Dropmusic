<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Music | DropMusic</title>
    <jsp:include page="header.jsp"/>
    <script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
    <script>
        $(document).ready(function() {
            $("#playButton").click(function(){
                $.ajax({
                    type: 'POST',
                    url:'playAction.action?inputModel.artistName='+ document.getElementById('artist').value
                        + '&inputModel.albumTitle='+document.getElementById('album').value
                        + '&inputModel.title='+document.getElementById('title').value,
                    dataType: 'text',
                    success: function(data){
                        document.getElementById('playBar').innerHTML = data;
                    }, error: function(data) {
                        document.getElementById('playBar').innerHTML = data;
                    }
                });
                return false;
            });
        });
    </script>
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

            <s:set var="artistName">${item.artistName}</s:set>
            <s:set var="albumName">${item.albumTitle}</s:set>
            <s:set var="title">${item.title}</s:set>

            <s:form action="playAction">
                <s:hidden name="inputModel.artistName" value="%{#artistName}" id="artist"/>
                <s:hidden name="inputModel.albumTitle" value="%{#albumName}" id="album"/>
                <s:hidden name="inputModel.title" value="%{#title}" id="title" />
                <button type="button" id="playButton">Play song!</button>
                <div id="playBar">

                </div>
            </s:form>
            <br />
        </c:forEach>
    </c:otherwise>
</c:choose>

</body>
</html>
