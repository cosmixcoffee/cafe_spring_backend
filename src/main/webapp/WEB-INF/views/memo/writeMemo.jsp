<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function() {
    let today = new Date().toISOString().split("T")[0];
    $("#w_date").val(today);
});
$(function(){
	$("#btn_save").click(function(){
		let memo_title = $("#memo_title").val();
		let memo_content = $("#memo_content").val();
		let w_date = $("#w_date").val();
		
		if(memo_title == ""){
			alert("제목을 입력해주세요.");
			$("#memo_title").focus();
			return;
		}
		if(memo_content == ""){
			alert("내용을 입력해주세요.");
			$("#memo_content").focus();
			return;
		}
		if(w_date == ""){
			alert("날짜를 선택해주세요.");
			$("#w_date").focus();
			return;
		}
		
		let form_data = new FormData(document.getElementById("data_memo"));
		
		$.ajax({
			url : "/insertMemo.do",
			type : "POST",
			data : form_data,
			processData: false,
			contentType : false,
			success: function(response){
				console.log("전송 성공: ", response);
				alert("메모가 추가되었습니다.");
				//이전페이지로 or 검색창으로
			    var cf_number = $("#cf_number").val();
			    var userid = $("#userid").val();
			    // cf_name은 만약 hidden 필드가 없다면, JSP EL로 처리하거나 별도로 저장한 변수를 사용합니다.
			    var cf_name = "${param.cf_name}";
			    
			    window.location.href = "/listMemo.do?cf_number=" + encodeURIComponent(cf_number) +
			                             "&cf_name=" + encodeURIComponent(cf_name) +
			                             "&userid=" + encodeURIComponent(userid);
			},
			error: function(error){
				console.error("전송 실패 : ", error);
				alert("메모 추가에 실패했습니다.");
			}
		});
	});
});
</script>

<meta charset="UTF-8">
<title>CAFETITLE</title>
</head>
<body>
<%@ include file="../include/login_menuplus.jsp"%>

<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">메모작성</h2>
            </div>
        </div>

<div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">

<!-- 인식 id는 memodata enctype 속성을 설정해서 multipart  -->
<!-- name = 서버로 데이터 전송시 해당 변수 인식 id = html 문서 내에서 식별   -->
<form id = "data_memo" enctype = "multipart/form-data" method = "post">
	<!-- 카페 number를 이용한 카페 이름을 코드와 함께 이전 페이지에서 받아와야 한다. -->
	<table border = "1" style = "width : 600px; height : auto;">
		<!-- 히든으로 폼을 해치지 않으면서 폼데이터에 포함되어 전송되도록 한다. -->
		<input type = "hidden" id = "cf_number" name = "cf_number" value = "${param.cf_number}">
		<input type = "hidden" id = "userid" name = "userid" value = "${param.userid}">
		<!-- 위쪽에 카페 제목을 추가시켜줘야한다. -->	
		<tr>
			<td>카페명</td>
			<!-- 해당 카페명 이전 페이지든 어디서든 id를 이용해서 받아오도록 한다. -->
			<td style = "width : 300px">${param.cf_name}</td>
			<td>작성자</td>
			<!-- 작성자도 마찬가지 -->
			<td>${param.userid}</td>
		</tr>
		<tr>
			<td>메모 제목</td>
			<td colspan = "3" ><input type = "text" id = "memo_title" name = "memo_title" placeholder = "제목"></td>
		</tr>
		<tr>
			<td>작성일</td>
			<td colspan = 3><input type = "date" id = "w_date" name = "w_date"></td>
		</tr>
		<tr>
			<td>첨부파일(사진)</td>	
			<td colspan = 3><input type = "file" name = "memo_file"></td>
		</tr>
		<tr>
			<td>내용</td>	
			<td colspan = 3><textarea name = "memo_content" id = "memo_content" placeholder = "내용" style = "width : 99%; height : 200px;"></textarea></td>
		</tr>
		<tr>
			<!-- height 등을 100%설정해줘서 비율로 크기를 맞춰줄수 있다 -> %사용 -->
			<td colspan = 4 align = "right"><button type = "button" id = "btn_save" style = "height : 50px; width : 100%;" >저장</button></td>
		</tr>
	</table>
</form>
</div>
</div>
</div>
</body>
</html>