<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CAFETITLE</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>

<script>
$(function () {
    // 로그인 버튼 클릭 시 페이지 이동
    $("#linkpwdBtn").click(function () {
        location.href = "/log/pwdFind"; // 이동할 JSP 경로
    });
	
    $("#linklogBtn").click(function () {
        location.href = "/log/login"; // 이동할 JSP 경로
    });
	
	$("#findIdBtn").click(function(){
		const name = $("#name").val().trim();
		const tel = $("#tel").val().trim();
		const email = $("#email").val().trim();
		const params = {"name":name, "tel":tel, "email":email};

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
		
		$.ajax({
			type: "POST",
			url: "/findID.do",
			data: params, 
			success: function(response){
				$("#resultDiv").html(response);	
			},
            error: function (xhr, status, error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.\n" + error);
            }		
		});
		
	});   
});
</script>
<body>
<%@ include file="/WEB-INF/views/include/login_menuplus.jsp" %>
<div style="display: flex; justify-content: center; margin-top: 25px">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
            <div style="flex: 1; text-align: left;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">아이디 찾기</h2>
            </div>
        </div>
        <form method="post" name="form1" action="/findID.do">
            <table border="1">

                <tr>
                    <td style="width: 140px;">이름</td>
                    <td style="width: 240px;padding-left: 33px;"><input type="text" id="name"></td>
                </tr>
                <tr>
                    <td>연락처</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="tel"></td>
                </tr>
                <tr>
                    <td>이메일</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="email"></td>
                </tr>
            </table>
            <div align="center" style="margin-top: 10px;">
            	<div id="resultDiv"></div>
            </div>
            
            <div style="margin-top: 10px; justify-content: center; align-items: center; flex-direction: column;" align="center">
                <input type="button" id="findIdBtn" value="아이디 찾기">
                <input type="button" id="linkpwdBtn" value="비밀번호 찾기">
                <input type="button" id="linklogBtn" value="로그인">
            </div>
        </form>
    </div>
</div>
</body>
</html>