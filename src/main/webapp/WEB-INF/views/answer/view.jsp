<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function() {
    listReply(1);
    listAttach();
    
    $("#btnList").click(function(event){
        event.preventDefault(); // 기본 이동 방지

        $.ajax({
            type: "GET",
            url: "/board/list.do",
            success: function(response) {
                $("#resultDiv").html(response); // `list.jsp`를 `resultDiv`에 로드
            },
            error: function(xhr, status, error) {
                console.error("AJAX 요청 실패:", status, error);
                alert("목록을 불러오는 데 실패했습니다.");
            }
        });
    });
});
//댓글 목록 불러오기
function listReply(page) {
    const board_idx = $("#btnReply").data("id"); // 버튼에 저장된 게시글 ID

    if (!board_idx) {
        console.error("게시글 ID 없음");
        return;
    }

    console.log("댓글 목록 불러오기: board_idx=", board_idx, "페이지:", page);

    $.ajax({
        url: "/reply/list.do",
        type: "GET",
        data: { board_idx: board_idx, curPage: page },
        success: function (result) {
            $("#listReply").html(result);
        },
        error: function (xhr, status, error) {
            console.error("댓글 목록 불러오기 실패:", status, error);
        }
    });
}
//첨부파일 목록 불러오기
function listAttach(){
    $.ajax({
        type: "post",
        url:"/board/list_attach/${dto.idx}",
        success: function(list){
            $("#uploadedList").empty(); // 🔥 기존 파일 목록 비우기
            $(list).each(function(){
                const fileInfo = getFileInfo(this);
                let html = "<div><a href='" + fileInfo.get_link + "'>" + fileInfo.original_name + "</a>&nbsp;&nbsp;";
                html += "<a href='#' class='file_del' data-src='" + this + "'>[삭제]</a></div>";
                $("#uploadedList").append(html);
            });
        }
    });
}



//파일 정보 가져오기
function getFileInfo(file_name){
    const get_link = "/upload/display_file?file_name=" + file_name;
    const original_name = file_name.substr(file_name.indexOf("_") + 1);
    return { original_name: original_name, get_link: get_link, file_name: file_name };
}


</script>
<style>
.fileDrop {
    margin-top: 10px;
    width: 645px;
    height: 100px;
    border: 1px dotted gray;
    background-color: lightgray;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    color: white;
    font-size: 14px;
    position: relative;
}

/* 방법 1: CSS 가상 요소로 문구 추가 */
.fileDrop::before {
    content: "여기에 첨부할 이미지를 올려 놓으세요.";
    color: black;
    font-size: 14px;
}
</style>
</head>
<body>
<div  style="display: flex; justify-content: center;">
	<div id="title" align="center">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">건의사항</h2>
                <h2 id="titleMain" style="display: inline-block; font-size:15px ;margin: 0;">상세보기</h2>
            </div>
        </div>
	<form id="form1" name="form1" method="post" enctype="multipart/form-data">
		<input type="hidden" id="idx" name="idx" value="${dto.idx}">
		<input type="hidden" id="writer" name="writer" value="${dto.writer}">
		<table border="1" style="width: 700px;">
			<tr>
				<td align="center">작성자</td>
				<td >${dto.writer}</td>
				<td style="text-align: right; white-space: nowrap; width: 1%;">조회수</td>
				<td style="text-align: right; white-space: nowrap; width: 1%;">${dto.hit}</td>
			</tr>
			<tr>
				<td  align="center" style=" white-space: nowrap; width: 1%;">작성일자</td>
				<td colspan="3"><fmt:formatDate value="${dto.regdate}" pattern="yyyy-MM-dd a HH:mm:ss" /></td>
			</tr>
			<tr>
				<td  align="center">이름</td>
				<td colspan="3">${dto.writer}</td>
			</tr>
			<tr>
				<td  align="center">제목</td>
				<td colspan="3"><input style="width: 100%; box-sizing: border-box;" name="title" value="${dto.title}"></td>
			</tr>
			<tr>
				<td  align="center">내용</td>
				<td colspan="3"><textarea  rows="5" id="contents" name="contents" placeholder="내용을 입력하세요" style="width: 100%; box-sizing: border-box;">${dto.contents}</textarea></td>
			</tr>
		</table>
		    <div id="uploadedList" style="margin-top: 10px">
		        <c:forEach var="file" items="${attach_list}">
		            <div>
		                <a href="/upload/display_file?file_name=${file}">${file}</a>
		                <a href="#" class="file_del" data-src="${file}">[삭제]</a>
		            </div>
		        </c:forEach>
		    </div>
		<div>
		<div class="fileDrop" style="margin-top: 10px"></div>
		<div style="margin-top: 10px">
			<c:if test="${sessionScope.userid == dto.writer}">
				<button type="button" id="btnAnsUpdate">수정</button>
				<button type="button" id="btnAnsDelete">삭제</button>
			</c:if>
			<button type="button" id="btnList">목록</button>
		</div>
		</div>
	</form>
	<hr>

<div style=" margin-top: 40px;">
    <c:if test="${sessionScope.userid != null}">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
    <div style="flex: 1; text-align: left; font-size: 12px;">
        <h2 id="titleMain" style="display: inline-block; margin: 0;">댓글란</h2>
    </div>
        </div>
        <textarea style="width:700px " id="reply_text" placeholder="댓글을 작성하세요."></textarea>
        <br>
        <button type="button" id="btnReply" data-id="${dto.idx}">댓글쓰기</button>
    </c:if>
    
</div> 
<div id="listReply" style="margin-top: 20px"></div>
<!-- 댓글수정화면영역 -->
<div id="modifyReply" style="display: none; margin-top: 10px" ></div>
	</div>
</div>
</body>
</html>