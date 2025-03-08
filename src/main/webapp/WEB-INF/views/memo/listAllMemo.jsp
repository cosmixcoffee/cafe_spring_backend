<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<script src = "http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function() {
    $(".delete_memo").click(function() {
        // data-memo_idx 속성에서 메모 번호 가져오기
        let memo_idx = $(this).attr("data-memo_idx");
        var userid = "${sessionScope.userid}";
        var cf_number = "${param.cf_number}";
        
        // AJAX 요청
        $.ajax({
            type: "GET", // GET 방식으로 설정
            url: "/deleteMemo.do", // 요청 URL
            data: { "memo_idx": memo_idx, "userid": userid, "cf_number": cf_number  },
            success: function(response) {
                console.log("메모가 삭제되었습니다.", response);
                alert("메모가 삭제되었습니다.");
                // location.reload() 대신 AJAX로 memo 목록을 다시 불러오기
                $.ajax({
                    type: "GET",
                    url: "/listMemo.do",
                    data: { "userid": "${userid}", "cf_number": "${cf_number}", "cf_name": "${cf_name}" },
                    success: function(updatedList) {
                        $("#resultDiv").html(updatedList);
                    },
                    error: function(xhr, status, error) {
                        console.error("메모 목록 갱신 실패:", error);
                        alert("메모 목록 갱신에 실패했습니다.");
                    }
                });
            },
            error: function(xhr, status, error) {
                console.error("메모 삭제 실패.", error);
                alert("메모 삭제에 실패했습니다.");
            }
        });
    });
});
</script>

<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">메모목록</h2>
            </div>
        </div>
<div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">

${userid} 님의 메모목록에는 ${count}개의 메모가 등록되어 있습니다.
<!-- 카페 목록 출력 -->
<table border="1" style="margin-top: 10px;">
    <thead>
        <tr>
            <th>메모 번호</th>
            <th>유저ID</th>
            <!-- 여기서는 카페 명이지만 카페 number가 출력될것 카페 명으로 수정 필요 -->
            <th>카페명</th>
            <th colspan="2">작성 일자</th>
            
            
        </tr>
    </thead>
    <tbody>
    <c:if test="${not empty list}">
        <c:forEach var="memo" items="${list}" >
            <tr>

                <td>${memo.memo_idx}</td>
                <td>${memo.userid}</td>
                <td>${memo.cf_name}</td>
            	<td>${memo.w_date}</td>
            	<td><button class = "delete_memo" data-memo_idx = "${memo.memo_idx}">삭제</button></td>
            </tr>
        </c:forEach>
        </c:if>
        <c:if test="${empty list}">
        <tr>
        	<td colspan="9">등록된 메모가 없습니다.</td>
        </tr>
        </c:if>
    </tbody>
</table>
</div>
<div id="resultDiv"></div>
</div>
</div>
