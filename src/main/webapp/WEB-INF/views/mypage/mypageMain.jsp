<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>카페 정보</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function () {
    // 페이지 로드 시 profile.do 호출
    $.ajax({
        type: "GET",
        dataType: "json",
        url: "/profile.do?userid=${sessionScope.userid}&au_lv=${sessionScope.user_au_lv}",
        success: function (response) {
            console.log("서버 응답:", response); // 서버 응답 확인

            if (response.status === "success") {
                let au_lv = 0;
                
                // Manager 정보가 있을 경우
                if (response.manager) {
                    $("#userid").val(response.manager.userid);
                    $("#cf_name").val(response.manager.cf_name);
                    $("#name").val(response.manager.name);
                    $("#email").val(response.manager.email);
                    $("#tel").val(response.manager.tel);
                    au_lv = response.manager.au_lv;
                }
                
                // User 정보가 있을 경우
                if (response.user) {
                    $("#userid").val(response.user.userid);
                    $("#name").val(response.user.name);
                    $("#email").val(response.user.email);
                    $("#tel").val(response.user.tel);
                    au_lv = response.user.au_lv;
                }
                
                // Admin 정보가 있을 경우
                if (response.admin) {
                    $("#userid").val(response.admin.userid);
                    $("#name").val(response.admin.name);
                    $("#email").val(response.admin.email);
                    $("#tel").val(response.admin.tel);
                    au_lv = response.admin.au_lv;
                }

                // LV 값에 따른 카페 이름 행 숨김/표시
                if (au_lv !== 1) {
                    $("#cfNameRow").show(); // lv === 1이면 표시
                } else {
                    $("#cfNameRow").hide(); // lv !== 1이면 숨김
                }
            } else {
                if (response.redirectUrl) {
                    window.location.href = response.redirectUrl;
                } else {
                    // 오류 메시지만 출력
                    alert(response.message);
                }
            } // **여기 닫는 중괄호 추가**
        },
        error: function (xhr, status, error) {
            console.error("AJAX 요청 오류:", status, error);
            alert("정보를 불러오는 중 오류가 발생했습니다.");
        }
    }); // **여기 닫는 세미콜론 추가**
});

</script>
</head>
<body>

<div style="display: flex; justify-content: center;">
<div id="title">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; margin-top: 20px;">
        <div style="flex: 1; text-align: left; font-size: 12px;">
            <c:choose>
                <c:when test="${sessionScope.user_au_lv == 0}">
                   <h2 style="display: inline-block; margin: 0;">${sessionScope.user_name} 관리자님 환영합니다.</h2>
                </c:when>
                <c:when test="${sessionScope.user_au_lv == 1}">
                    <h2 style="display: inline-block; margin: 0;">${sessionScope.user_name} 매니저님 환영합니다.</h2>
                </c:when>
                <c:when test="${sessionScope.user_au_lv == 2}">
                    <h2 style="display: inline-block; margin: 0;">${sessionScope.user_name} 회원님 환영합니다.</h2>
                </c:when>

            </c:choose>
        </div>
    </div>

<form>
  <table border="1">
    <c:choose>
        <c:when test="${sessionScope.user_au_lv == '1'}">
            <tr id="cfNameRow">
                <td style="width: 140px;">운영 중인 카페</td>
                <td style="width: 240px;" colspan="2"><input type="text" id="cf_name" style="width: 220px;" readonly></td>
            </tr>
        </c:when>
    </c:choose>
    <tr>
        <td style="width: 140px;">아이디</td>
        <td style="width: 240px;" colspan="2"><input type="text" id="userid" style="width: 220px;" readonly></td>
    </tr>
    <tr>
        <td style="width: 140px;">이름</td>
        <td style="width: 240px;"><input type="text" id="name" style="width: 220px;" readonly></td>
    </tr>
    <tr>
        <td style="width: 140px;">이메일</td>
        <td style="width: 240px;" colspan="2"><input type="text" id="email" style="width: 220px;" readonly></td>
    </tr>
    <tr>
        <td style="width: 140px;">연락처</td>
        <td style="width: 240px;" colspan="2"><input type="text" id="tel" style="width: 220px;" readonly></td>
    </tr>
</table>

</form>
</div>
</div>
</body>
</html>
