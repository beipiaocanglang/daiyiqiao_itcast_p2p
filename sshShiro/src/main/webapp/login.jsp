<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	<s:actionerror />
	<form method="post"
		action="${pageContext.request.contextPath}/user/login">
		username:<input type="text" name="username"><br>
		password:<input type="password" name="password"><br> <input
			type="submit" value="LOGIN">
	</form>
</body>
</html>