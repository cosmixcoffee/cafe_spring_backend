<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>카페 정보</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function () {
    // 페이지 로드 시 checkcafe.do 호출
	$.ajax({
	    type: "GET",
	    dataType: "json",
	    url: "/checkcafe.do?userid=${sessionScope.userid}",
	    success: function (response) {
	        console.log("카페서버 응답:", response); // 서버 응답 확인
			
	        // JSON 데이터 직접 사용
	        if ( response.status === "success") {
	        	$("#userid").val(response.manager.userid);
	            $("#cf_name").val(response.manager.cf_name);
	            $("#cf_code").val(response.manager.cf_code);
	            $("#cf_pcode").val(response.manager.cf_pcode);
	            $("#cf_adr1").val(response.manager.cf_adr1);
	            $("#cf_adr2").val(response.manager.cf_adr2);
	            $("#cf_tel").val(response.manager.cf_tel);
	        } else {
	            alert(response.message); // 오류 메시지 출력
	        }
	    },
	    error: function (xhr, status, error) {
	        console.error("AJAX 요청 오류:", status, error);
	        alert("카페 정보를 불러오는 중 오류가 발생했습니다.");
	    }
	});
    
	// 사업자 번호 중복체크
    $("#codecheckBtn").click(function () {
        const $cf_code = $("#cf_code");
        const cf_codeVal = $cf_code.val();

        if (!cf_codeVal || typeof cf_codeVal !== "string") {
            alert("사업자 번호를 입력하세요.");
            $ .focus();
            return;
        }

        $.ajax({
            type: "POST",
            url: "/codeCheck.do",
            data: { "cf_code": cf_codeVal.trim()},
            success: function (txt) {
                if (txt === "available") {
                    alert("사용 가능한 사업자 번호입니다.");
                    isIdAvailable = true;
                } else {
                    alert("이미 등록을 한 사업자 번호입니다.");
                    isIdAvailable = false;
                }
            },
            error: function (xhr, status, error) {
                console.error("Ajax 오류:", error);
                alert("아이디 중복 확인 중 오류가 발생했습니다.");
            },
        });
    });
    
    
    // 카페등록
    $("#cfinBtn").click(function () {
    	
    	const cf_name = $("#cf_name").val().trim();
    	const userid = $("#userid").val().trim();
		const cf_code = $("#cf_code").val().trim();
		const cf_pcode = $("#cf_pcode").val().trim();
		const cf_adr1 = $("#cf_adr1").val().trim();
		const cf_adr2 = $("#cf_adr2").val().trim();
		const cf_tel = $("#cf_tel").val().trim();
		const cf_content = $("#cf_content").val().trim();

        if (!cf_code) {
            alert("사업번호를 입력해주세요.");
            $("#cf_code").focus();
            return;
        }

        const params = {
        	"cf_name": cf_name,
        	"userid": userid,
        	"cf_code": cf_code,
        	"cf_pcode": cf_pcode,
        	"cf_adr1": cf_adr1,
        	"cf_adr2": cf_adr2,
        	"cf_tel": cf_tel,
        	"cf_content": cf_content
        };
        $.ajax({
            type: "POST",
            url: "/cafein.do",
            data: params,
            success: function (response) {
                alert(response.message); // 서버에서 받은 메시지 표시
                
                if (response.cf_number) {
                    sessionStorage.setItem("cf_number", response.cf_number);
                }
                
                if (response.redirect) {
                    window.location.href = response.redirect; // 서버에서 받은 URL로 이동
                }
            },
            error: function () {
                alert("카페 정보 등록 중 오류가 발생했습니다.");
            },
        });
    });
});
</script>
</head>
<body>
<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">카페등록</h2>
            </div>
        </div>
<form>
    <table border="1">
        <tr>
            <td style="width: 140px;">카페 이름</td>
            <td style="width: 240px;" colspan="2"><input type="text" id="cf_name" style="width: 220px;" readonly></td>
        </tr>
        <tr>
            <td style="width: 140px;">담당 매니저</td>
            <td style="width: 240px;"  colspan="2"><input type="text"  id="userid" style="width: 220px;" readonly></td>
        </tr>
		<tr>	
            <td style="width: 140px;">사업자번호</td>
            <td style="width: 240px;"><input type="text" id="cf_code" style="width: 220px;"></td>
            <td><input type="button" id="codecheckBtn" value="중복체크"></td>
        </tr>
        <tr>
            <td style="width: 140px;">우편번호</td>
            <td style="width: 240px;"  colspan="2"><input type="text" id="cf_pcode" style="width: 220px;" readonly></td>
        </tr>
        <tr>
            <td style="width: 140px;">카페주소</td>
            <td style="width: 240px;" colspan="2"><input type="text" id="cf_adr1" style="width: 220px;" readonly></td>
        </tr>
        <tr>
            <td style="width: 140px;">상세주소</td>
            <td style="width: 240px;"  colspan="2"><input type="text" id="cf_adr2" style="width: 220px;" readonly></td>
        </tr>
        <tr>
            <td style="width: 140px;">전화번호</td>
            <td style="width: 240px;"  colspan="2"><input type="text" id="cf_tel" style="width: 220px;" readonly></td>
        </tr>
		<tr>
            <td style="width: 140px;">카페 소개문</td>
            <td style="width: 240px;" colspan="2"><input type="text" id="cf_content" style="width: 220px;" ></td>
        </tr>
    </table>
       <div style="margin-top: 10px; justify-content: center; align-items: center; flex-direction: column;" align="center">
           <input type="button" id="cfinBtn" value="등록">
       </div>
</form>
</div>
</div>
</body>
</html>
