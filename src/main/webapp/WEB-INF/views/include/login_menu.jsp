<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">

    <div style="text-align: left; font-size: 20px;">
        <a href="http://localhost/main" style="color: black; text-decoration: none; font-weight: bold; display: inline-block; min-width: 120px;">
            <!-- CAFETITLE을 숨기지만 폭을 유지 -->
        </a>
    </div>
	<div style="text-align: right;font-size: 14px">
	<c:choose>
	    <c:when test="${sessionScope.userid == null}">
	    	<a href="/log/login" style="color: black; text-decoration: none;">로그인</a>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${sessionScope.user_au_lv  == '0'}">
                ${sessionScope.user_name} 관리자님 |
            </c:when>
            <c:when test="${sessionScope.user_au_lv == '1'}">
                ${sessionScope.user_name} 매니저님 |
            </c:when>
            <c:when test="${sessionScope.user_au_lv == '2'}">
                ${sessionScope.user_name} 님 |
            </c:when>
        </c:choose>
        <a href="/logout.do" style="color: black; text-decoration: none;">로그아웃</a> |
        <a href="/mypage/mypage" style="color: black; text-decoration: none;">마이페이지</a>
    </c:otherwise>
</c:choose>
</div>
</div>
<hr>
