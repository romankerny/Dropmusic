<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Manage</title>
</head>
<body>

<!-- Artists -->

<s:form method="POST" action="addArtist">
    <s:textfield name="manageModel.name" label="Name" />
    <s:textfield name="manageModel.details" label="Details" />
    <s:submit label="Submit" />
</s:form>

<!-- Albuns -->

<s:form method="POST" action="addAlbum">
    <s:textfield name="manageModel.title" label="title" />
    <s:textfield name="manageModel.description" label="Description" />
    <s:textfield name="manageModel.genre" label="Genre" />
    <s:textfield name="manageModel.launchDate" label="Launch Date" />
    <s:textfield name="manageModel.editorLabel" label="Editor Label" />
    <s:submit label="Submit" />
</s:form>

<!-- Music -->

<s:form method="POST" action="addMusic">
    <s:textfield name="manageModel.track" label="Track" />
    <s:textfield name="manageModel.title" label="Title" />
    <s:textfield name="manageModel.lyrics" label="Lyrics" />
    <s:submit label="Submit" />
</s:form>


</body>
</html>
