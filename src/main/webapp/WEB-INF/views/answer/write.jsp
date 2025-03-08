<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>

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
<div id="maintitle">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain1" style="display: inline-block; margin: 0;">건의사항</h2>
                <h2 id="titleMain2" style="display: inline-block; font-size:15px ;margin: 0;">글쓰기</h2>
            </div>
        </div>
<form id="form1" name="form1" method="post" action="/board/insert.do">
<table border="1">
<div>
	<tr>
		<td>제목</td>
		<td width="600px">
			<input name="title"id="title" placeholder="제목을 입력하세요." style="width: 100%; box-sizing: border-box;" >
		</td>
	</tr>
	<tr>
		<td>내용</td>
		<td>
			<textarea rows="5" id="contents" name="contents" placeholder="내용을 입력하세요" style="width: 100%; box-sizing: border-box;"></textarea>
		</td>
	</tr>
</div>
</table>
<div align="center">
	<div class="fileDrop">
	</div>
	<div id="uploadedList"></div>
	<button style="button; margin-top: 10px" id="btnSave">확인</button>
</div>
</form>
</div>
</div>
</body>
</html>