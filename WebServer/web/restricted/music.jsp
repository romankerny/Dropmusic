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


        $(document).ready(function(){

            $("#associateDropboxButton").click(function() {

                $.ajax({
                type: 'POST',
                    url:'associateMusicAction.action?' +
                        '&albumTitle='+ document.getElementById('albumName').innerText +
                        '&artistName=' + document.getElementById('artistName').innerText +
                        '&musicTitle='+ document.getElementById('musicTitle').innerText +
                        '&fileName='+ document.getElementById('fileName').value
                ,
                dataType: 'text',
                success: function(data){
                    document.getElementById('rsp').innerHTML = data;
                }, error: function(data) {
                    document.getElementById('rsp').innerHTML = data;
                }
                });
                return false;
            });


            $("#shareDropboxButton").click(function() {

                $.ajax({
                    type: 'POST',
                    url:'shareMusicAction.action?' +
                        '&email='+ document.getElementById('email').value +
                        '&albumTitle='+ document.getElementById('albumName').innerText +
                        '&artistName=' + document.getElementById('artistName').innerText +
                        '&musicTitle='+ document.getElementById('musicTitle').innerText
                    ,
                    dataType: 'text',
                    success: function(data){
                        document.getElementById('rspShare').innerHTML = data;
                    }, error: function(data) {
                        document.getElementById('rspShare').innerHTML = data;
                    }
                });
                return false;
            });



        })

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

            <s:submit type="button" action="playAction" ></s:submit>
            <br />
            <br/>

            <p hidden id="artistName" >${item.artistName}</p>
            <p hidden id="albumName" >${item.albumTitle}</p>
            <p hidden id="musicTitle" >${item.title}</p>

        </c:forEach>
        <s:text name="Name of file in DropBox" />
        <s:textfield id="fileName" />
        <button type="button" id="associateDropboxButton">Associate</button>
        <div id="rsp"></div><br>


        <hr>
        <s:text name="Share with" />
        <s:textfield id="email" />
        <button type="button" id="shareDropboxButton">Share</button>
        <div id="rspShare"></div><br>

    </c:otherwise>
</c:choose>

</body>
</html>
