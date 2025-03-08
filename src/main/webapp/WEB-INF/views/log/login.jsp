<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CAFETITLE</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function(){
    $("#loginBtn").click(function(event) {
        event.preventDefault(); // ✅ 폼 자동 제출 방지

        const userid = $("#userid").val().trim();
        const passwd = $("#passwd").val().trim();
        const option = $("#option").val();
        let params = {"userid": userid, "passwd": passwd, "option": option};

        if(userid === ""){
            alert("아이디를 입력하세요.");
            $("#userid").focus();
            return;
        }
        if(passwd === ""){
            alert("비밀번호를 입력하세요.");
            $("#passwd").focus();
            return;
        }

        $.ajax({
            type: "POST",
            url: "/login.do",
            data: params,
            dataType: "json",
            success: function (res) {
                if (res.status === "success") {
                    alert(res.message);  // "로그인 성공" 메시지 출력
                    console.log("로그인 성공 → 페이지 이동 시작");
                    setTimeout(function() { 
                        window.location.href = "http://localhost/main";  // ✅ 0.5초 후 강제 이동
                    }, 500);
                } else {
                    alert(res.message);
                }
            },
            error: function (xhr, status, error) {
                console.error("로그인 오류:", error);
            },
        });

    });
});
</script>
<style>
    .full-width-button {
        width: 100%;
        padding: 1px;
        font-size: 16px;
        border: none;
        cursor: pointer;
    }
</style>
</head>
<body>

<%@ include file="/WEB-INF/views/include/login_menu.jsp" %>
<div align="center">
    <h2>
        <a href="http://localhost/main" style="color: inherit; text-decoration: none;">CAFETITLE</a>
    </h2>
    <form action="/login.do" method="post"> <!-- ✅ action 추가 -->
        <table border="1">
            <tr>
                <td>사용자 유형</td>
                <td>
                    <select id="option" name="option"> <!-- ✅ id 추가 -->
                        <option value="user">사용자</option>
                        <option value="manager">매니저</option>
                        <option value="admin">관리자</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>아이디</td>
                <td><input type="text" id="userid" name="userid" required></td> <!-- ✅ id 추가 -->
            </tr>
            <tr>
                <td>비밀번호</td>
                <td><input type="password" id="passwd" name="passwd" required></td> <!-- ✅ id 추가 -->
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" id="loginBtn" value="로그인" class="full-width-button">
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <a href="/log/idFind" style="color: black; text-decoration: none;">아이디</a>
                    <a href="/log/pwdFind" style="color: black; text-decoration: none;">/ 비밀번호 찾기</a>
                    <a href="/log/join" style="color: black; text-decoration: none;">/ 회원가입</a>
                </td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>
