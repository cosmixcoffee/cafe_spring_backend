<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
        /* 별점 전체 컨테이너 */
        .star-rating {
            display: flex; /* 별들을 가로로 나열 */
        }

        /* 별점 입력 요소 스타일 */
        .star {
            appearance: none; /* 기본 브라우저 스타일 제거 (라디오 버튼 숨김) */
            padding: 1px; /* 약간의 패딩 추가 */
        }

        /* 별의 기본 상태 (안 채워진 별) */
        .star::after {
            content: '☆'; /* 빈 별 표시 */
            color: hsl(60, 80%, 45%); /* 노란색 톤 색상 */
            font-size: 20px; /* 별 크기 설정 */
        }

        /* 마우스를 올리거나 체크된 별의 상태 */
        .star:hover::after,               /* 마우스를 올린 별 */
        .star:has(~ .star:hover)::after, /* 마우스 올린 별 이전의 별들 */
        .star:checked::after,            /* 선택된 (체크된) 별 */
        .star:has(~ .star:checked)::after { /* 선택된 별 이전의 별들 */
            content: '★'; /* 채워진 별로 변경 */
        }

        /* 마우스를 올린 별 이후의 별 상태 */
        .star:hover ~ .star::after {
            content: '☆'; /* 이후의 별은 빈 별로 유지 */
        }
    </style>
</head>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
//오늘을 기본 날짜로 설정
function redirectBack(){
	history.back();
}
$(function() {
    let today = new Date().toISOString().split("T")[0];
    $("#w_date").val(today);
});
$(function(){
	$("#btn_save").click(function(){
		let userid = $("#userid").val();  // 히든 필드에서 사용자 ID 가져오기
	    console.log("현재 로그인된 사용자 ID:", userid);
		let rv_content = $("#rv_content").val();
		let w_date = $("#w_date").val();
		let cf_number = $("#cf_number").val(); 
		
		if(rv_content == ""){
			alert("내용을 입력해주세요.");
			$("#rv_content").focus();
			return;
		}
		if(w_date == ""){
			alert("날짜를 선택해주세요.");
			$("#w_date").focus();
			return;
		}
		
		let form_data = new FormData(document.getElementById("data_review"));
		
		$.ajax({
			url : "/insertReview.do",
			type : "POST",
			data : form_data,
			processData: false,
			contentType : false,
			success: function(response){
				console.log("전송 성공: ", response);
				console.log("데이터: ", form_data);
				
				if(response.status === "success") {
                    let updatedCount = response.review.cf_rv_count;  // ✅ 업데이트된 리뷰 개수 가져오기
                    let cf_number = response.review.cf_number;

                    alert("리뷰가 추가되었습니다.");

                    // ✅ cf_rv_count 업데이트
                    $(".cafe-row[data-cf_number='" + cf_number + "'] .cf_rv_count").text(updatedCount);

                    // ✅ 1초 후 `cafe_detail.do` 페이지로 이동
                    setTimeout(function () {
                        window.location.href = "/cafe_detail.do?cf_number=" + encodeURIComponent(cf_number);
                    }, 1000);
                } else {
                    alert("리뷰 추가에 실패했습니다. " + response.message);
                }
            },
			error: function(error){
				console.error("전송 실패 : ", error);
				alert("리뷰 추가에 실패했습니다.");
			}
		});
	});
	
});
</script>
<body>
<%@ include file="../include/login_menuplus.jsp"%>
<c:out value="${param.cf_number}"/>

<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">메모작성</h2>
            </div>
        </div>
<div align = "center">
<!-- 인식 id는 memodata enctype 속성을 설정해서 multipart  -->
<!-- name = 서버로 데이터 전송시 해당 변수 인식 id = html 문서 내에서 식별   -->
<form id = "data_review" enctype = "multipart/form-data" method = "post">
	<!-- 카페 number를 이용한 카페 이름을 코드와 함께 이전 페이지에서 받아와야 한다. -->
	<table border = "1" style = "width : 600px; height : auto;">
		<!-- 히든으로 폼을 해치지 않으면서 폼데이터에 포함되어 전송되도록 한다. -->
		<input type = "hidden" id = "cf_number" name = "cf_number" value = "${param.cf_number}">
		<input type = "hidden" id = "userid" name = "userid" value = "${sessionScope.userid}">
		<!-- 위쪽에 카페 제목을 추가시켜줘야한다. -->	
		<tr>
			<td>카페명</td>
			<!-- 해당 카페명 이전 페이지든 어디서든 id를 이용해서 받아오도록 한다. -->
			<td style = "width : 300px">${cf_name}</td>
			<td>작성자</td>
			<!-- 작성자도 마찬가지 -->
			<td>${userid}</td>
		</tr>
		<tr>
			<td>작성일</td>
			<td colspan = 3><input type = "date" id = "w_date" name = "w_date"></td>
		</tr>
		<tr>
			<td>첨부파일(사진)</td>	
			<td colspan = 3><input type = "file" name = "rv_filename"></td>
		</tr>
		<tr>
			<td colspan = "4" style = "width:100%; height:auto;">
			<div class="star-rating">
        	<!-- 각 별을 라디오 버튼으로 구현 -->
        		<input type="radio" class="star" value="1" name="cf_point"> <!-- 첫 번째 별 -->
        		<input type="radio" class="star" value="2" name="cf_point"> <!-- 두 번째 별 -->
        		<input type="radio" class="star" value="3" name="cf_point"> <!-- 세 번째 별 -->
        		<input type="radio" class="star" value="4" name="cf_point"> <!-- 네 번째 별 -->
        		<input type="radio" class="star" value="5" name="cf_point"> <!-- 다섯 번째 별 -->
    		</div>
    		</td>
		</tr>
		<tr>
			<td>내용</td>	
			<td colspan = 3><textarea name = "rv_content" id = "rv_content" placeholder = "내용" style = "width : 99%; height : 200px;"></textarea></td>
		</tr>
		<tr>
			<!-- height 등을 100%설정해줘서 비율로 크기를 맞춰줄수 있다 -> %사용 -->
			<td colspan = 4 align = "right"><button type = "button" id = "btn_save" style = "height : 50px; width : 100%;" >저장</button></td>
		</tr>
	</table>
</form>
</div>
</body>
</html>