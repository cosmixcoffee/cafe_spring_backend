<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 결과가 있을 경우 -->
<c:if test="${not empty userpwd}">
    <b>${userpwd.USERID}</b>
    <c:choose>
        <c:when test="${au_lv == 2}">회원님의</c:when>
        <c:when test="${au_lv == 1}">카페매니저님의</c:when>
        <c:otherwise>사용자의</c:otherwise>
    </c:choose>
    임시 비밀번호가 <b>${email}</b>로 전송되었습니다.
</c:if>

<!-- 결과가 없을 경우 -->
<c:if test="${empty userpwd}">
    <p style="color: red;">${message}</p>
</c:if>
