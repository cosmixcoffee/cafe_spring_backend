<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>카페 정보</title>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<!-- cf_number, cf_name을 넘겨줘야한다. -->
<script>

//파트에 찾기
function openPopup(imagePath) {
	window.open(imagePath, '_blank', 'width=600,height=600');
}


$(function() {
    list(1);  // 페이지 로드 시 첫 번째 페이지의 리뷰 목록을 불러옴
});

// 후기 목록 불러오기
function list(cur_page) {
    let cf_number = "${cafeInfo.cf_number}".trim();

    if (!cf_number || cf_number === "null") {
        console.log("cf_number가 null이므로 후기 요청을 건너뜁니다.");
        return;
    }

    let params = { "cur_page": cur_page, "cf_number": cf_number };

    $.ajax({
        type: "get",
        url: "/listAllReview.do",
        data: params,
        success: function(html) {
            console.log("후기 데이터 응답:", html); // 디버깅용
            $("#resultDiv").html(html);
        },
        error: function(xhr, status, error) {
            console.error("후기 요청 실패:", error);
            alert("후기 데이터를 불러오는 데 실패했습니다.");
        }
    });
}

// 후기 작성 버튼 클릭 시 writeReview.jsp로 이동
$(document).on("click", ".addReview", function () {
    let cf_number = $(this).data("cf_number"); 
    let cf_name = $(this).data("cf_name");   
    let userid = $(this).data("userid");    

    console.log(cf_name, userid, cf_number);

    // 페이지 이동 처리
    window.location.href = "/review/writeReview?cf_number=" + encodeURIComponent(cf_number) +
                             "&cf_name=" + encodeURIComponent(cf_name) +
                             "&userid=" + encodeURIComponent(userid);
});


</script>
<%@ include file="../style/cf_detail_st.jsp"%>
</head>
<%@ include file="../include/login_menuplus.jsp"%>
<body>
	<div class="container">
		<div class="header">
			<h2>${cafeInfo.cf_name}</h2>
		</div>
		<!-- 이미지 세션 -->
		<div class="image-section">
			<!-- 메인이미지 -->
			<div class="main-image">
				<c:choose>
					<c:when test="${not empty mainImagePath}">
						<img src="${mainImagePath}" alt="메인이미지"
							onclick="openPopup('${mainImagePath}')">
					</c:when>
					<c:otherwise>
                        메인 이미지 없음
                    </c:otherwise>
				</c:choose>
			</div>

			<!-- 서브이미지 -->
			<div class="sub-images">
				<c:forEach var="subImagePath" items="${subImagePaths}"
					varStatus="status">
					<div class="sub-image">
						<img src="${subImagePath}" alt="서브이미지 ${status.index + 1}"
							onclick="openPopup('${subImagePath}')">
					</div>
				</c:forEach>
			</div>
		</div>

		<!-- 정보 세션 -->
		<div class="info-section">
			<div class="info-row">
				<div class="label">담당자:</div>
				<div class="value">${cafeInfo.userid}</div>
			</div>
			<div class="info-row">
				<div class="label">주소:</div>
				<div class="value">${cafeInfo.cf_adr1}${cafeInfo.cf_adr2}</div>
			</div>
			<div class="info-row">
				<div class="label">연락처:</div>
				<div class="value">${cafeInfo.cf_tel}</div>
			</div>
			<div class="info-row">
				<div class="label">카페 소개문:</div>
				<div class="value">${cafeInfo.cf_content}</div>
			</div>
			<div>
				<table align="center">
					<tr>
						<td align = "center" style="font-weight: bold; padding : 20px; ">후기</td>
					</tr>
					<c:if test="${not empty sessionScope.userid}">
					    <tr>
					        <td align="center" colspan="2">
					        	<button type="button" class="addReview"
					        	    data-cf_number="${cafeInfo.cf_number}" 
					        	    data-cf_name="${cafeInfo.cf_name}" 
					        	    data-userid="${sessionScope.userid}">
					        		후기 작성
					        	</button>
					        </td>
					    </tr>
					</c:if>
					<tr>
						<td colspan="2">
							<div id="resultDiv"></div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

</body>
</html>
