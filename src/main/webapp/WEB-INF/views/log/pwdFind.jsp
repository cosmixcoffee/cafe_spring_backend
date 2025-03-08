<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>비밀번호 찾기</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<script>
$(function () {
    $("#linkidBtn").click(function () {
        location.href = "/log/idFind"; // 아이디 찾기 페이지 이동
    });

    $("#linklogBtn").click(function () {
        location.href = "/log/login"; // 로그인 페이지 이동
    });

    $("#findPwdBtn").click(function () {
        const userid = $("#userid").val().trim();
        const name = $("#name").val().trim();
        const tel = $("#tel").val().trim();
        const email = $("#email").val().trim();

        if (!userid) {
            alert("아이디를 입력하세요.");
            return;
        }
        if (!name) {
            alert("이름을 입력하세요.");
            return;
        }
        if (!tel) {
            alert("연락처를 입력하세요.");
            return;
        }
        if (!email) {
            alert("이메일을 입력하세요.");
            return;
        }

        const params = {
            userid: userid,
            name: name,
            tel: tel,
            email: email
        };

        $.ajax({
            type: "POST",
            url: "/findPWD.do",
            data: params, // JSON이 아닌 Form 데이터 방식으로 전송
            success: function (response) {
                $("#resultDiv").html(response); // JSP 결과 페이지 로드
            },
            error: function (xhr, status, error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.\n" + error);
            }
        });
    });
});
</script>
<body>
<%@ include file="../include/login_menuplus.jsp" %>
<div style="display: flex; justify-content: center; margin-top: 25px">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
            <div style="flex: 1; text-align: left;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">비밀번호 찾기</h2>
            </div>
        </div>
        <table border="1">
            <tr>
                <td style="width: 140px;">아이디</td>
                <td style="width: 240px;padding-left: 33px;">
                    <input type="text" id="userid">
                </td>
            </tr>
            <tr>
                <td style="width: 140px;">이름</td>
                <td style="width: 240px;padding-left: 33px;">
                    <input type="text" id="name">
                </td>
            </tr>
            <tr>
                <td>연락처</td>
                <td colspan="2" style="padding-left: 33px;">
                    <input type="text" id="tel">
                </td>
            </tr>
            <tr>
                <td>이메일</td>
                <td colspan="2" style="padding-left: 33px;">
                    <input type="text" id="email">
                </td>
            </tr>
        </table>
        <div style="margin-top: 10px; text-align: center;">
            <input type="button" id="linkidBtn" value="아이디 찾기">
            <input type="button" id="findPwdBtn" value="비밀번호 찾기">
            <input type="button" id="linklogBtn" value="로그인">
        </div>
            <div id="resultDiv" style="margin-top: 10px; text-align: center; display: flex; justify-content: center;"></div>

        </div>
    </div>
</body>
</html>
