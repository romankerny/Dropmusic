<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
    <jsp:include page="header.jsp"/>
    <title>Manage | DropMusic</title>
    <script><%@include file="../js/notification.js"%></script>
    <script>setEmail("${session.email}")</script>
    <script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
    <script>

        $(document).ready(function(){

            $("#searchAlbumButton").click(function() {

                $.ajax({
                    type: 'POST',
                    url:'addAlbum.action?manageModel.artist='+ document.getElementById('AAartistName').value
                        + '&manageModel.title='+document.getElementById('AAtitle').value
                        + '&manageModel.description='+document.getElementById('AAdescription').value
                        + '&manageModel.genre='+document.getElementById('AAgenre').value
                        + '&manageModel.launchDate='+document.getElementById('AAlaunchDate').value
                        + '&manageModel.editorLabel='+document.getElementById('AAeditorLabel').value
                    ,
                    dataType: 'text',
                    success: function(data){
                        document.getElementById('rspAlb').innerHTML = data;
                    }, error: function(data) {
                        document.getElementById('rspAlb').innerHTML = data;
                    }
                });
                return false;
            });

            $("#searchArtistButton").click(function() {

                $.ajax({
                    type: 'POST',
                    url:'addArtist.action?manageModel.name=' + document.getElementById('AartistName').value
                        +'&manageModel.details=' + document.getElementById('AartistDetails').value,
                    dataType: 'text',
                    success: function(data){
                        document.getElementById('rspArt').innerHTML = data;
                    }, error: function(data) {
                        document.getElementById('rspArt').innerHTML = data;
                    }
                });
                return false;
            });

            $("#searchMusicButton").click(function() {

                $.ajax({
                    type: 'POST',
                    url:'addMusic.action?manageModel.track=' + document.getElementById('Mtrack').value
                        +'&manageModel.title='+document.getElementById('Mtitle').value
                        +'&manageModel.lyrics='+document.getElementById('Mlyrics').value
                        +'&manageModel.albumTitle='+document.getElementById('MalbumTitle').value
                        +'&manageModel.artistName='+document.getElementById('MartistName').value
                    ,
                    dataType: 'text',
                    success: function(data){
                        document.getElementById('rspMus').innerHTML = data;
                    }, error: function(data) {
                        document.getElementById('rspMus').innerHTML = data;
                    }
                });
                return false;
            });




        })





    </script>
</head>
<body>

<!-- Artists -->
<div id="artist">
    <h1>Artist</h1>
    <s:form method="GET">

    <table style="width:500px">
        <tr><td>Name:</td>    <td><s:textfield id="AartistName" /></td></tr>
        <tr><td>Details:</td> <td><s:textfield id="AartistDetails" /></td></tr>
    </table>
        <button type="button" id="searchArtistButton">Add</button>
    </s:form>
    <div id="rspArt"></div>
</div>


<!-- Albuns -->
<div id="album">
<h1>Album</h1>
<s:form method="GET">
    <table style="width:500px">
        <tr><td>Artist:</td>       <td><s:textfield id="AAartistName" /></td></tr>
        <tr><td>Title:</td>        <td><s:textfield id="AAtitle" /></td></tr>
        <tr><td>Description:</td>  <td><s:textfield id="AAdescription" /></td></tr>
        <tr><td>Genre:</td>        <td><s:textfield id="AAgenre" /></td></tr>
        <tr><td>Launch Date:</td>  <td><s:textfield id="AAlaunchDate" /></td></tr>
        <tr><td>Editor Label:</td> <td><s:textfield id="AAeditorLabel" /></td></tr>
    </table>
    <button type="button" id="searchAlbumButton">Add</button>
    <div id="rspAlb"></div>
</s:form>
</div>


<!-- Music -->
<h1>Music</h1>
<div id="Music">
<s:form method="GET">
    <table style="width:500px">
    <tr><td>Artist Name:</td>  <td><s:textfield id="MartistName" /></td></tr>
    <tr><td>Album Name:</td>   <td><s:textfield id="MalbumTitle" /></td></tr>
    <tr><td>Track:</td>        <td><s:textfield id="Mtrack" /></td></tr>
    <tr><td>Title:</td>        <td><s:textfield id="Mtitle" /></td></tr>
    <tr><td>Lyrics:</td>       <td><s:textfield id="Mlyrics" /></td></tr>
    </table>
    <button type="button" id="searchMusicButton">Add</button>
    <div id="rspMus"></div>
</s:form>
</div>



</body>
</html>
