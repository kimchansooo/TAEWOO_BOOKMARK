<%@page import="kr.or.kosa.dto.Book"%>
<%@page import="java.util.List"%>
<%@page import="kr.or.kosa.dao.BookDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<%
	BookDao dao = new BookDao();
	List<Book> list  = dao.BookAlllist();
	System.out.println(list);
%>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<<<<<<< HEAD
<<<<<<< HEAD
		<h3>BookDaoTest</h3>
		<c:forEach var="book" items="<%=list %>">
			isbn:${book.isbn }<br>
		</c:forEach>
=======
		<h3>test</h3>
			<a href="${pageContext.request.contextPath}/calendarall.do">DEMO WEBSITE</a>
>>>>>>> 113de6d2741c28707980591198824c72456eea0f
=======
>>>>>>> bfe324407f72000cb800dd4bd6ca0d74da1616b0
</body>
</html>