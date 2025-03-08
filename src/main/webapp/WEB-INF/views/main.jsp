<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CAFETITLE</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function() {
    $("#searchBtn").click(function() {
		list("1");
    });
    
    
    // 선택 삭제 버튼 클릭 이벤트
    $("#btnDelete").click(function() {
        let selectedIds = [];
        $(".delete-checkbox:checked").each(function() {
            selectedIds.push($(this).val());
        });

        if (selectedIds.length === 0) {
            alert("삭제할 카페를 선택하세요.");
            return;
        }

        if (!confirm("카페를 삭제하시겠습니까?")) {
            return;
        }

        // AJAX 요청으로 삭제 수행
        $.ajax({
            type: "DELETE",
            url: "/deleteSelected.do",
            data: { ids: selectedIds.join(",") },
            success: function(response) {
                if (response.trim() === "success") {
                    alert("선택한 게시글이 삭제되었습니다.");
                    list("1");
                } else {
                    alert("삭제 중 문제가 발생했습니다.");
                }
            },
            error: function(xhr, status, error) {
                console.error("삭제 요청 실패:", status, error);
                alert("삭제 요청 처리 중 문제가 발생했습니다.");
            }
        });
    });
});

function list(cur_page){
	let search=$("#search").val();
	let searchkey=$("#searchkey").val();
	let params = {"cur_page": cur_page, "search": search, "searchkey": searchkey};
	if (!search.trim()) {
        alert("검색어를 입력하세요.");
        $("#search").focus(); 
        return; 
    }
	
	console.log(params);
	
    $.ajax({
        type:"get",
        url: "/search_list.do", 
        data: params,
        success: function(html) {
/* 에러날때체크용     	console.log(html); */
    		$("#resultDiv").html(html);
        }
    });	
}

</script>
</head>
<body>
<%@ include file="./include/login_menu.jsp" %>
<div align="center">
<h2>
<a href="http://localhost/main" style="color: inherit; text-decoration: none;">CAFETITLE</a>
</h2>
<div style="margin-bottom: 10px">
    <select name="searchkey" id="searchkey">
        <c:if test="${searchkey == 'CF_NAME' || searchkey == null}">
            <option value="cf_name" selected>카페명</option>
            <option value="cf_adr1">주소</option>
            <option value="all">카페명+주소</option>
        </c:if>
        <c:if test="${searchkey == 'cf_adr1' }">
            <option value="cf_name">카페명</option>
            <option value="cf_adr1" selected>주소</option>
            <option value="all">카페명+주소</option>
        </c:if>
        <c:if test="${searchkey == 'ADDRESS_NAME' }">
            <option value="cf_name">카페명</option>
            <option value="cf_adr1">주소</option>
            <option value="all" selected>카페명+주소</option>
        </c:if>
    </select>
    <input type="text" id="search" value="${search}">
    <input type="button" id="searchBtn" value="검색">
    <c:if test="${sessionScope.user_au_lv == 0}">
	    <button type="button" id="btnDelete">삭제</button>
	    <br><br>
	</c:if>
	</div>
	<div id="resultDiv"></div>
</div>
</body>
</html>