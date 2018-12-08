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
        <s:text name="Name:"    /><s:textfield     id="AartistName" /><br>
        <s:text name="Details:" /><s:textfield     id="AartistDetails" /><br>
        <button type="button" id="searchArtistButton">Search</button>
    </s:form>
    <div id="rspArt"></div>
</div>


<!-- Albuns -->
<div id="album">
<h1>Album</h1>
<s:form method="GET">
    <s:text name="Artist:" />       <s:textfield id="AAartistName" /><br>
    <s:text name="Title:" />        <s:textfield id="AAtitle" /><br>
    <s:text name="Description:" />  <s:textfield id="AAdescription" /><br>
    <s:text name="Genre:" />        <s:textfield id="AAgenre" /><br>
    <s:text name="Launch Date:" />  <s:textfield id="AAlaunchDate" /><br>
    <s:text name="Editor Label:" /> <s:textfield id="AAeditorLabel" /><br>
    <button type="button" id="searchAlbumButton">Search</button>
    <div id="rspAlb"></div>
</s:form>
</div>


<!-- Music -->
<h1>Music</h1>
<div id="Music">
<s:form method="GET">
    <s:text name="Artist Name:" /> <s:textfield id="MartistName" /><br>
    <s:text name="Album Name:" />  <s:textfield id="MalbumTitle" /><br>
    <s:text name="Track:" />       <s:textfield id="Mtrack" /><br>
    <s:text name="Title:" />       <s:textfield id="Mtitle" /><br>
    <s:text name="Lyrics:" />      <s:textfield id="Mlyrics" /><br>
    <button type="button" id="searchMusicButton">Search</button>
    <div id="rspMus"></div>
</s:form>
</div>



</body>
</html>
