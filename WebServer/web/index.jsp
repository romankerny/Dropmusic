<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Login</title>
  <!-- Required meta tags -->
</head>
<body>
<s:form action="loginAction" method="POST">
  <s:textfield name="email" label="Email" />
  <s:password name="password" label="Password" />
  <s:submit label="Log in" />
</s:form>

<s:form action="register-direct" method="post">
<s:submit value="Sign Up"/>
</s:form><br>

<s:form action="associateDropBoxAction" method="post">
  <s:submit value="Sign Up w/ Dropbox" />
</s:form>




</body>
</html>