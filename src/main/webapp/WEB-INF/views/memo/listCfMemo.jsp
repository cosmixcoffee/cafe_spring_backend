<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
function moveData(cf_name, userid, cf_number){
	window.location.href="../memo/writeMemo?cf_name=" + encodeURIComponent(cf_name) + "&userid=" + encodeURIComponent(userid) + "&cf_number=" + encodeURIComponent(cf_number);
}  

$(function () {
    // .delete_memo 클래스가 있는 경우에만 실행
    if ($(".delete_memo").length > 0) {
        $(".delete_memo").click(function () {
            let memo_idx = $(this).attr("data-memo_idx");
            if (memo_idx) {
                $.ajax({
                    type: "GET",
                    url: "/deleteMemo.do",
                    data: { "memo_idx": memo_idx, "userid": "${userid}", "cf_number": "${cf_number}"  },
                    success: function (response) {
                        console.log("메모가 삭제되었습니다.", response);
                        alert("메모가 삭제되었습니다.");
                        location.reload(); // 페이지 새로 고침
                    },
                    error: function (xhr, status, error) {
                        console.error("메모 삭제 실패.", error);
                        alert("메모 삭제에 실패했습니다.");
                    }
                });
            } else {
                console.warn("memo_idx가 존재하지 않습니다.");
            }
        });
    }
    
});
</script>
<head>
<%@ include file="../include/login_menuplus.jsp"%>
<meta charset="UTF-8">
<title>카페 메모</title>
</head>
<body>
<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">카페 메모</h2>
            </div>
        </div>

	<div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
		
		<!-- 카페 목록 출력 -->
		<table border="1" style ="width : 600px; height : auto; margin-top: 10px;">
			<thead>
				<tr>
					<td colspan = "4" align = "center">
						<strong>${cf_name}</strong>
					</td>
				</tr>
				<tr>
					<td colspan = "4" align = "right">
						작성한 메모: ${count}
					</td>
				</tr>
				<tr>
					<th>메모 제목</th>
					<th>유저ID</th>
					<th>작성일자</th>
					<th><button style = "width : 100%; height : 100%;"id="writeMemo" onclick = "moveData('${cf_name}','${userid}','${cf_number}')">메모작성</button></th>
					
				</tr>
			</thead>
			<tbody>
				<c:if test="${not empty list}">
					<c:forEach var="memo" items="${list}">
						<tr align = "center">
							<td>
								<a href = "/memoDetail.do?memo_idx=${memo.memo_idx}"style="color: black; text-decoration: none;">
									<strong>${memo.memo_title}</strong> (${memo.memo_idx})
								</a>
							</td>
							<td>${memo.userid}</td>
							<td>${memo.w_date}</td>
							<td><button class="delete_memo" style = "width : 100%; height : 100%;"
									data-memo_idx="${memo.memo_idx}">삭제</button></td>
						</tr>
					</c:forEach>
				</c:if>
				<c:if test="${empty list}">
					<tr>
						<td colspan="4">등록된 메모가 없습니다.</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	</div>
	</div>
</body>
</html>