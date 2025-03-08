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
$(document).ready(function () {
    $("#searchForm").submit(function (event) {
        event.preventDefault(); // 🔥 기본 폼 제출 방지 (페이지 새로고침 방지)

        const formData = $(this).serialize(); // 폼 데이터 직렬화

        $.ajax({
            type: "GET",
            url: "/board/list.do",
            data: formData, // 검색어 전달
            success: function (response) {
                $("#resultDiv").html(response); // 🔥 검색 결과를 `#resultDiv`에 출력
                sessionStorage.setItem("searchExecuted", "true"); // 🔥 검색 실행 여부 저장
            },
            error: function (xhr, status, error) {
                console.error("AJAX 검색 요청 실패:", status, error);
                alert("검색에 실패했습니다.");
            }
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
                <h2 id="titleMain" style="display: inline-block; margin: 0;">건의사항</h2>
            </div>
        </div>
	<div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
		<c:set var="user_au_lv" value="${sessionScope.user_au_lv}" />
		<form id="searchForm">
		<select name="search_option">
			<option value="all" <c:out value="${map.search_option == 'all' ? 'selected' : '' }" /> >이름+제목</option>
			<option value="name" <c:out value="${map.search_option == 'name' ? 'selected' : '' }" /> >이름</option>
			<option value="title" <c:out value="${map.search_option == 'title' ? 'selected' : '' }" /> >제목</option>
		</select>
		<input type="text" name="keyword">
		<input type="submit" value="조회">
		<c:if test="${sessionScope.userid != null}">
			<button type="button" id="btnWrite">글쓰기</button>
		</c:if>
		</form>
		${map.count}개의 게시물이 있습니다.
		<table border="1" width="600px" style="text-align: center;">
			<tr>
				<th>번호</th>
				<th>제목</th>
				<th>이름</th>
				<th>날짜</th>
				<th>조회수</th>
			</tr>
		<c:forEach var="row" items="${map.list}">
			<tr>
				<td>${row.idx}</td>
				<td>
				    <a href="#" class="view-board"
				       data-idx="${row.idx}"
				       data-cur_page="${map.page_info.curPage}"
				       data-search_option="${map.search_option}"
				       data-keyword="${map.keyword}">
				        ${row.title}
				    </a>
				    <c:if test="${row.cnt > 0}">
				        <span style="color: red">(${row.cnt})</span>
				    </c:if>
				</td>
				<td>${row.name}</td>
				<td><fmt:formatDate value="${row.regdate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td>${row.hit}</td>
			</tr>
		</c:forEach>
			<tr>
				<td colspan="5" align="center">
					<c:if test="${map.page_info.curPage > 1}">
						<a href="javascript:list('${map.page_info.prevPage}')">[이전]</a>
					</c:if>
					<c:forEach var="num" begin="${map.page_info.blockBegin}" end="${map.page_info.blockEnd}">
						<c:choose>
							<c:when test="${ num == map.page_info.curPage}">
								<span style="color: red">${num}</span>&nbsp;
							</c:when>
							<c:otherwise>
								<a href="javascript:list('${num}')">${num}</a>&nbsp;
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${map.page_info.curBlock <= map.page_info.totBlock}">
						<a href="javascript:list('${map.page_info.nextPage}')">[다음]</a>
					</c:if>
					<c:if test="${map.page_info.curPage <= map.page_info.totPage}">
						<a href="javascript:list('${map.page_info.totPage}')">[끝]</a>
					</c:if>
				</td>
			</tr>
		</table>
	</div>
	</div>
</div>
</body>
</html>