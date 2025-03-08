<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CAFETITLE</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
let isIdAvailable = false;

$(function () {
    // 선택 버튼 클릭 이벤트
    $("#checkBtn").click(function (event) {
        event.preventDefault(); // ✅ 폼 자동 제출 방지

        const selectedOption = $("input[name='option']:checked").val();

        if (!selectedOption) {
            alert("사용자 유형을 선택해주세요.");
            return;
        }

        if (selectedOption === "user") {
            $("#titleSub").text("일반 회원가입");
            $("#resultDiv").html("");
        } else if (selectedOption === "manager") {
            $("#titleSub").text("카페 매니저 회원가입");
            $.ajax({
                type: "GET",
                url: "/log/join_cfinfo",
                success: function (txt) {
                    $("#resultDiv").html(txt);
                },
                error: function (xhr, status, error) {
                    console.error("AJAX 요청 실패:", error);
                    alert("카페 매니저 정보를 불러오는 중 오류가 발생했습니다.");
                    $("#resultDiv").html(""); // 오류 발생 시 초기화
                },
            });
        }
    });

    // 아이디 중복체크 버튼 클릭 이벤트
    $("#idcheckBtn").click(function (event) {
        event.preventDefault(); // ✅ 폼 자동 제출 방지

        const $userid = $("#userid");
        const useridVal = $userid.val();

        if (!useridVal || typeof useridVal !== "string") {
            alert("아이디를 입력하세요.");
            $userid.focus();
            return;
        }

        if (useridVal.toLowerCase().includes("admin")) {
            alert("아이디에 'admin'을 포함할 수 없습니다.");
            $userid.focus();
            return;
        }

        const selectedOption = $("input[name='option']:checked").val();

        if (!selectedOption) {
            alert("가입 유형을 선택해주세요.");
            return;
        }

        $.ajax({
            type: "POST",
            url: "/idCheck.do",
            data: { "userid": useridVal.trim(), "selectedOption": selectedOption },
            success: function (txt) {
                if (txt === "available") {
                    alert("사용 가능한 아이디입니다.");
                    isIdAvailable = true;
                } else {
                    alert("이미 존재하는 아이디입니다.");
                    isIdAvailable = false;
                }
            },
            error: function (xhr, status, error) {
                console.error("Ajax 오류:", error);
                alert("아이디 중복 확인 중 오류가 발생했습니다.");
            },
        });
    });

    // 회원가입 버튼 클릭 이벤트
    $("#joinBtn").click(function (event) {
        event.preventDefault(); // ✅ 폼 자동 제출 방지
    	
        const $userid = $("#userid");
        const useridVal = $userid.val();

        if (!useridVal || typeof useridVal !== "string") {
            alert("아이디를 입력하세요.");
            $userid.focus();
            return;
        }

        if (useridVal.toLowerCase().includes("admin")) {
            alert("아이디에 'admin'을 포함할 수 없습니다.");
            $userid.focus();
            return;
        }

        const selectedOption = $("input[name='option']:checked").val();
        
        if (!selectedOption) {
            alert("가입 유형을 선택해주세요.");
            return;
        }

        if (!isIdAvailable) {
            alert("아이디 중복체크가 필요합니다.");
            return;
        }

        const params = collectFormData(selectedOption);
        if (!params) return; // 필수 필드 누락 시 종료

        $.ajax({
            type: "POST",
            url: "/join.do",
            data: params,
            dataType: "json", // 응답을 JSON으로 기대
            success: function (res) {
                if (res.status === "success") {
                    alert(res.message); // 성공 메시지 출력
                    
                    window.location.href = "http://localhost/log/login";
                } else if (res.status === "error") {
                    alert(res.message); // 에러 메시지 출력
                }
            },
            error: function (xhr, status, error) {
                console.error("회원가입 오류:", error);
                alert("회원가입 중 오류가 발생했습니다.");
            },
        });
    });
});

// collectFormData 함수 추가
function collectFormData(selectedOption) {
    const params = {};
    
    // 공통 필드 수집
    params.userid = $("#userid").val().trim();
    if (!params.userid) {
        alert("아이디를 입력해주세요.");
        $("#userid").focus();
        return null;
    }

    params.passwd = $("#passwd").val().trim();
    if (!params.passwd) {
        alert("비밀번호를 입력해주세요.");
        $("#passwd").focus();
        return null;
    }

    params.name = $("#name").val().trim();
    if (!params.name) {
        alert("이름을 입력해주세요.");
        $("#name").focus();
        return null;
    }

    params.email = $("#email").val().trim();
    if (!params.email) {
        alert("이메일(e-mail)을 입력해주세요.");
        $("#email").focus();
        return null;
    }

    params.tel = $("#tel").val().trim();
    if (!params.tel) {
        alert("연락처를 입력해주세요.");
        $("#tel").focus();
        return null;
    }

    params.selectedOption = selectedOption;

    // 선택된 옵션에 따라 추가 데이터 수집
    if (selectedOption === "manager") {
        params.cf_name = $("#cf_name").val().trim();
        if (!params.cf_name) {
            alert("카페 이름을 입력해주세요.");
            $("#cf_name").focus();
            return null;
        }

        params.cf_pcode = $("#cf_pcode").val().trim();
        if (!params.cf_pcode) {
            alert("우편번호를 입력해주세요.");
            $("#cf_pcode").focus();
            return null;
        }

        params.cf_adr1 = $("#cf_adr1").val().trim();
        if (!params.cf_adr1) {
            alert("주소를 입력해주세요.");
            $("#cf_adr1").focus();
            return null;
        }

        params.cf_adr2 = $("#cf_adr2").val() ? $("#cf_adr2").val().trim() : ""; // 기본값 설정

        params.cf_tel = $("#cf_tel").val().trim();
        if (!params.cf_tel) {
            alert("카페 연락처를 입력해주세요.");
            $("#cf_tel").focus();
            return null;
        }

        params.au_lv = 1; // Manager 레벨
    } else if (selectedOption === "user") {
        params.au_lv = 2; // User 레벨
    }

    return params; // 수집된 데이터 반환
}
</script>
</head>
<body>
<%@ include file="/WEB-INF/views/include/login_menuplus.jsp" %>

<div style="display: flex; justify-content: center; align-items: center; flex-direction: column;" align="center">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
            <div style="flex: 1; text-align: left;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">회원가입</h2>
                <h2 id="titleSub" style="display: inline-block; font-size: 0.9em; color: black; margin: 0; margin-left: 5px;"></h2>
            </div>
        </div>
        <form method="post" name="form1" action="/join.do">
            <table border="1">
                <tr>
                    <td style="width: 140px;">가입 유형</td>
                    <td style="width: 240px; text-align: center;">
                        <label><input type="radio" name="option" value="user">일반 회원</label>
                        <label><input type="radio" name="option" value="manager">카페 매니저</label>
                    </td>
                    <td>
                        <input type="button" id="checkBtn" value="선택" style="width: 100%;">
                    </td>
                </tr>
                <tr>
                    <td>아이디</td>
                    <td style="padding-left: 33px;"><input type="text" id="userid"></td>
                    <td><input type="button" id="idcheckBtn" value="중복체크"></td>
                </tr>
                <tr>
                    <td>비밀번호</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="password" id="passwd"></td>
                </tr>
                <tr>
                    <td>이름</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="name"></td>
                </tr>
                <tr>
                    <td>이메일(e-mail)</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="email"></td>
                </tr>
                <tr>
                    <td>연락처</td>
                    <td colspan="2" style="padding-left: 33px;"><input type="text" id="tel"></td>
                </tr>
            </table>
        </form>
            <div style="margin-top: 20px;" align="center" id="resultDiv"></div>
		        <div style="margin-top: 5px;" align="center">
		            <input type="button" id="joinBtn" value="회원가입">
				</div>
    </div>
</div>
</body>
</html>
