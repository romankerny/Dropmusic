<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Login</title>


</head>

<body>

<table width="100%">

<s:form action="loginAction" method="POST">
  <tr><td>Email: </td><td><s:textfield name="email" label="Email" /></td></tr>
  <tr><td>Password: </td><td><s:password name="password" label="Password" /></td></tr>
  <tr><td> <s:submit value="Log in" /></td></tr>
</s:form>

<s:form action="register-direct" method="post">
<tr><td><s:submit value="Sign Up"/></td></tr>
</s:form>

<s:form action="associateDropBoxAction" method="post">
  <tr><td><s:submit value="Login w/ Dropbox" /></td> </tr>
</s:form>

</table>
</body>
</html>