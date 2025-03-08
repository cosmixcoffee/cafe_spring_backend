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
$(document).ready(function () {
    // 페이지 로드 시 profile.do 호출
    let isPasswordVerified = false; // 비밀번호 확인 상태
    
 // 비밀번호 입력 후 확인 버튼을 눌렀을 때
    $("#checkPasswordBtn").click(function () {
        const passwd = $("#checkPasswd").val().trim();
        const userid = "${sessionScope.userid}";

        if (!passwd) {
            alert("비밀번호를 입력하세요.");
            return;
        }

        $.ajax({
            type: "POST",
            url: "/inputPassword.do",
            data: { userid: userid, passwd: passwd },
            success: function (response) {
                if (response.status === "success") {
                    isPasswordVerified = true;
                    $("#passwdCheckSection").hide();
                    $("#editFormSection").show();
                } else {
                    alert(response.message);
                }
            },
            error: function () {
                alert("비밀번호 확인 중 오류 발생.");
            }
        });
    });
    
    $.ajax({
        type: "GET",
        dataType: "json",
        url: "/memberEditInfo.do?userid=${sessionScope.userid}&au_lv=${sessionScope.user_au_lv}",
        success: function (response) {
            console.log("서버 응답:", response); // 서버 응답 확인

            if (response.status === "success") {

                // Manager 정보가 있을 경우
                if (response.manager) {
                    $("#userid").val(response.manager.userid);
                    $("#passwd").val(response.manager.passwd);
                    $("#name").val(response.manager.name);
                    $("#email").val(response.manager.email);
                    $("#tel").val(response.manager.tel);
                }
                // User 정보가 있을 경우
                if (response.user) {
                    $("#userid").val(response.user.userid);
                    $("#passwd").val(response.user.passwd);
                    $("#name").val(response.user.name);
                    $("#email").val(response.user.email);
                    $("#tel").val(response.user.tel);
                }
                // Admin 정보가 있을 경우
                if (response.admin) {
                    $("#userid").val(response.admin.userid);
                    $("#passwd").val(response.admin.passwd);
                    $("#name").val(response.admin.name);
                    $("#email").val(response.admin.email);
                    $("#tel").val(response.admin.tel);
                }
                
            } else {
                if (response.redirectUrl) {
                    window.location.href = response.redirectUrl;
                } else {
                    // 오류 메시지만 출력
                    alert(response.message);
                }
            }
        },
        error: function (xhr, status, error) {
            console.error("AJAX 요청 오류:", status, error);
            alert("정보를 불러오는 중 오류가 발생했습니다.");
        }
    });
    
 // 정보 수정 버튼 클릭 시
	$("#updateBtn").click(function(){
		const name = $("#name").val().trim();
		const passwd = $("#passwd").val().trim();
		const email = $("#email").val().trim();
		const tel = $("#tel").val().trim();
		const userid = $("#userid").val().trim();
		const user_au_lv = ${sessionScope.user_au_lv};

		if (!name) {
			alert("이름을 입력하세요.");
            return;
		}
		if (!passwd) {
			alert("패스워드를 입력하세요.");
            return;
		}
		if (!email) {
			alert("이메일을 입력하세요.");
            return;
		}
		if (!tel) {
			alert("연락처를 입력하세요.");
            return;
		}
		
		const params = { "userid": userid, "user_au_lv": user_au_lv };

		if (passwd.trim() !== "") params["passwd"] = passwd;
		if (email.trim() !== "") params["email"] = email;
		if (tel.trim() !== "") params["tel"] = tel;


		
		$.ajax({
		    type: "POST",
		    url: "/memberEdit.do",
		    data: params, 
		    success: function (response) {
		        alert(response.message); // response는 객체이므로 message 프로퍼티를 가져와야 함
		        if (response.status === "success") {
		        	 location.href = "/logout.do";
		        }
		    },
		    error: function (xhr, status, error) {
		        console.error("AJAX 요청 실패:", status, error);
		        alert("회원정보 수정 중 오류가 발생했습니다.");
		    }
		});
	});   
    
});




</script>
<body>

<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">회원정보수정</h2>
            </div>
        </div>
        
        
        <!-- 비밀번호 확인 섹션 -->
        <div id="passwdCheckSection">
            <table border="1">
                <tr>
                    <td>비밀번호 입력</td>
                    <td>
                        <input type="password" id="checkPasswd">
                        <button type="button" id="checkPasswordBtn">확인</button>
                    </td>
                </tr>
            </table>
        </div>
        
        <!-- 정보 수정 폼 (비밀번호 확인 후 보임) -->
        <div id="editFormSection" style="display: none;">
        
            <table border="1">
                <tr>
                    <td style="width: 140px;">아이디</td>
                    <td style="width: 240px; padding-left: 33px;">
						<input type="text" id="userid" readonly>
                    </td>
                </tr>
                <tr>
                    <td style="width: 140px;">이름</td>
                    <td style="width: 240px; padding-left: 33px;">
						<input type="text" id="name" readonly>
                    </td>
                </tr>
                <tr>
                    <td>패스워드</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="password" id="passwd"></td>
                </tr>
                <tr>
                    <td>이메일</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="email"></td>
                </tr>
				<tr>
                    <td>연락처</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="tel"></td>
                </tr>
            </table>
            <div style="margin-top: 10px; justify-content: center; align-items: center; flex-direction: column;" align="center">
                <input type="button" id="updateBtn" value="정보수정">
            </div>
    	</div>
    </div>
</div>
</body>
</html>
