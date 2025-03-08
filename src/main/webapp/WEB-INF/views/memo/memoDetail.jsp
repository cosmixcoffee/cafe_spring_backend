<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import = "java.util.Base64" %>
<!DOCTYPE html>
<html>
<head>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>

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
                <h2 id="titleMain" style="display: inline-block; margin: 0;">메모 상세보기</h2>
            </div>
        </div>
<div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
	<c:if test = "${not empty memo}">
		<table border = "1" style = "width : 600px; height : auto;">
			<tr align = "center" >
				<td  colspan = "2">
					<strong>${memo.cf_name}</strong>
				</td>
			</tr>
			<tr align = "center" >
				<td>
					${memo.memo_title}
				</td>
				<td  style = "width : 100px; height : auto;">
					${memo.w_date}
				</td>
			</tr>
		</table>
		<table border = "1" style = "width:600px; height : auto;">
			<tr>
				<td>
					<c:if test = "${not empty encoded}">
						<!-- Base64 인코딩 및 이미지 출력 -->
						<img src = "data:image/jpeg;base64,${encoded}" alt = "첨부된 이미지" style = "max-width:100%; height: auto;">
					</c:if>
					<c:if test = "${empty encoded}">
						<p>첨부된 이미지가 없습니다.</p>
					</c:if>
				</td>
				<td style = "width : 400px; height : auto;" align = "center" >
					${memo.memo_content}
				</td>
			</tr>
		</table>		
	</c:if>
<c:if test="${empty memo}">
    <h2>메모를 찾을 수 없습니다.</h2>
    <p>요청하신 메모가 존재하지 않거나 삭제되었습니다.</p>
</c:if>
</div>
</div>
</div>
</body>
</html>