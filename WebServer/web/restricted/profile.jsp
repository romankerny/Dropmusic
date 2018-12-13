<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <jsp:include page="header.jsp"/>
    <script><%@include file="../js/notification.js"%></script>
    <script>setEmail("${session.email}")</script>
    <title>Profile | DropMusic</title>

    <script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
    <script>

        $(document).ready(function(){

            $("#promoteButton").click(function() {

                $.ajax({
                    type: 'POST',
                    url:'turnIntoEditorAction.action?regular='+ document.getElementById('regular').value
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

            $("#associateDropboxButton").click(function() {

                $.ajax({
                    type: 'POST',
                    url:'associateMusicAction.action?' +
                        '&albumTitle='+ document.getElementById('albumTitle').value +
                        '&artistName=' + document.getElementById('aristName').value +
                        '&musicTitle='+ document.getElementById('musicTitle').value +
                        '&fileName='+ document.getElementById('musicFileName').value
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




        })


    </script>
</head>


<body>

<h1>Profile</h1>
<h2>Email:${session.email} </h2>


<h2>Turn a user into Editor</h2>
<s:form method="get">
    <s:text name="Email:"     />
    <s:textfield id="regular" />
    <button type="button" id="promoteButton">Promote</button>
</s:form>
<div id="rsp"></div>

<hr>

<s:form action="associateDropBoxAction" method="post">
    <s:text name="Associate with DropBox" />
    <s:submit />
</s:form>

<hr>

<h1>Associate Music : Dropbox - DropMusic</h1>
<s:form action="associateMusicAction" method="get">
    <s:text name="Music name:" /> <s:textfield id="musicTitle" /> <br>
    <s:text name="Album:" /> <s:textfield id="albumTitle" /> <br>
    <s:text name="Artist:" /> <s:textfield id="artistName" /> <br>
    <s:text name="Music file name: "/> <s:textfield id="musicFileName"/> <br>
    <button type="button" id="associateDropboxButton">Associate</button>
</s:form>

<hr>

</body>
</html>
