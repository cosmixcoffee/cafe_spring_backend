<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>

<body>

<textarea id="detail_replytext" rows="3" style="width: 700px">${dto.reply_text}</textarea>
<div style="text-align: center">
<c:if test="${sessionScope.userid == dto.replyer}">
	<button id="btnReplyUpdate" type="button">수정</button>
	<button id="btnReplyDelete" type="button">삭제</button>
</c:if>
<button id="btnReplyClose" type="button">닫기</button>
</div>
</body>
</html>