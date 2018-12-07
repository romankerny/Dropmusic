<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
    <title>Manage</title>
</head>
<body>

<!-- Artists -->
<div id="artist">
    <h1>Artist</h1>
    <s:form method="GET" action="addArtist">
        <s:text name="Name:" /><s:textfield name="manageModel.name" label="Name" /><br>
        <s:text name="Details:" /><s:textfield name="manageModel.details" label="Details" /><br>
        <s:submit label="Submit" /><br>
    </s:form>
</div>

<!-- Albuns -->
<h1>Album</h1>
<s:form method="GET" action="addAlbum">
    <s:text name="Artist:" /> <s:textfield name="manageModel.artist" label="Artist" /><br>
    <s:text name="Title:" /><s:textfield name="manageModel.title" label="title" /><br>
    <s:text name="Description:" /><s:textfield name="manageModel.description" label="Description" /><br>
    <s:text name="Genre:" /><s:textfield name="manageModel.genre" label="Genre" /><br>
    <s:text name="Launch Date:" /><s:textfield name="manageModel.launchDate" label="Launch Date" /><br>
    <s:text name="Editor Label:" /> <s:textfield name="manageModel.editorLabel" label="Editor Label" /><br>
    <s:submit label="Submit" /><br>
</s:form>

<!-- Music -->
<h1>Music</h1>
<s:form method="GET" action="addMusic">
    <s:textfield name="manageModel.track" label="Track" /><br>
    <s:textfield name="manageModel.title" label="Title" /><br>
    <s:textfield name="manageModel.lyrics" label="Lyrics" /><br>
    <s:submit label="Submit" /><br>
</s:form>


</body>
</html>
