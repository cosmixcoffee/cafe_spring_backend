<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 리스트가 있을 경우 출력 -->
<c:if test="${not empty userids}">
    <c:forEach var="user" items="${userids}">
    	<b>${user.NAME}</b>님의 아이디는 <b>${user.USERID}</b>입니다.<br>
    <c:choose>
        <c:when test="${user.AU_LV == 2}">
            일반회원으로 가입하셨습니다.
        </c:when>
        <c:when test="${user.AU_LV == 1}">
            카페매니저로 가입하셨습니다.
        </c:when>
    </c:choose>
    </c:forEach>
   </c:if>
<!-- 리스트가 없을 경우 메시지 출력 -->
<c:if test="${empty userids}">
	<p>${message}</p>
</c:if>
