<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	<shiro:hasRole name="admin">
		<a href="${pageContext.request.contextPath}/book/update">book
			update</a>
		<br>
		<a href="${pageContext.request.contextPath}/book/delete">book
			delete</a>
		<br>
		<a href="${pageContext.request.contextPath}/book/add">book add</a>
		<br>
	</shiro:hasRole>
	<a href="${pageContext.request.contextPath}/book/find">book find</a>
	<br>
</body>
</html>