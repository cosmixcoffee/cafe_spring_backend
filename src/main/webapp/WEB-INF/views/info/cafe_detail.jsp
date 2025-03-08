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
	function moveData(cf_name, cf_number) {
		window.location.href = "/cafe_search/review/writeReview.jsp?cf_name="
				+ encodeURIComponent(cf_name) + "&cf_number="
				+ encodeURIComponent(cf_number);
	}
	$(function() {
		let cf_number = "${cafeInfo.cf_number}";

		if (cf_number) {
			$.ajax({
				type : "POST", // 콜론(:) 사용
				url : "/listAllReview.do",
				data : {
					cf_number : cf_number
				}, // JSON 형식으로 key-value 전달
				success : function(resp) {
					console.log("후기 리스트: ", resp);
					$("#resultDiv").html(resp);
				},
				error : function(xhr, status, error) {
					console.error("후기 요청 실패:", error);
					alert("후기 데이터를 불러오는 데 실패했습니다.");
				},
			});
		} else {
			console.log("cf_number가 null입니다.");
		}
	});
</script>
<style>
.container {
	width: 900px; /* 고정 크기 */
	height: 350px;
	margin: 0 auto;
	padding: 20px;
	font-family: Arial, sans-serif;
}

.header {
	text-align: left; /* 왼쪽 정렬 */
	margin-bottom: 20px;
}

.image-section {
	display: flex;
	justify-content: space-between;
	margin-bottom: 10px; /* 간격 조정 */
	gap: 10px; /* 메인이미지와 서브이미지 간격 조정 */
}

.main-image {
	width: 600px;
	height: 350px;
	display: flex;
	justify-content: center;
	overflow: hidden;
	box-sizing: border-box; /* 크기 유지 */
}

.main-image img {
	max-width: 100%;
	max-height: 100%;
	object-fit: cover; /* 박스에 꽉 차도록, 이미지가 잘릴 수 있음 */
}

.sub-images {
	display: grid;
	grid-template-columns: 1fr 1fr; /* 2열 */
	gap: 10px;
	max-height: 210px; /* 메인이미지와 동일한 높이 */
	overflow: hidden;
	flex-shrink: 0; /* 공간 부족 시 줄어들지 않게 */
}

.sub-image {
	width: 140px; /* 서브 이미지 크기 */
	height: 100px; /* 서브 이미지 크기 */
	display: flex;
	justify-content: center;
	overflow: hidden;
	box-sizing: border-box; /* 크기 유지 */
	border: 1px solid #ddd; /* 외형선 추가 */
}

.sub-image img {
	max-width: 100%;
	max-height: 100%;
	object-fit: cover; /* 박스에 꽉 차도록, 이미지가 잘릴 수 있음 */
	cursor: pointer;
}

.info-section {
	margin-bottom: 20px;
}

.info-row {
	display: flex; /* 한 줄로 배치 */
	margin-bottom: 10px;
	align-items: center; /* 텍스트 정렬 */
	text-align: left; /* 왼쪽 정렬 */
}

.label {
	font-weight: bold;
	margin-right: 10px; /* 라벨과 값 간격 */
}

.value {
	flex: 1;
	text-align: left; /* 컨테이너 내 왼쪽 정렬 */
}
</style>
<script>
	// 파트에 찾기
	function openPopup(imagePath) {
		window.open(imagePath, '_blank', 'width=600,height=600');
	}
</script>
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
					<c:when test="${not empty cafeImages.MAIN_IMAGE_PATH}">
						<img src="${cafeImages.MAIN_IMAGE_PATH}" alt="메인이미지"
							onclick="openPopup('${cafeImages.MAIN_IMAGE_PATH}')">
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
				<div class="value">${cafeInfo.ID}</div>
			</div>
			<div class="info-row">
				<div class="label">주소:</div>
				<div class="value">${cafeInfo.cf_adr1}${cafeInfo.cf_adr2}</div>
			</div>
			<div class="info-row">
				<div class="label">연락처:</div>
				<div class="value">${cafeInfo.CF_TEL}</div>
			</div>
			<div class="info-row">
				<div class="label">카페 소개문:</div>
				<div class="value">${cafeInfo.CF_CONTENT}</div>
			</div>
			<div>
				<table align="center">
					<tr>
						<td align = "center" style="font-weight: bold; padding : 20px; ">후기</td>
					</tr>

					<c:if test="${not empty sessionScope.user_id}">
						<tr>
							<td align="center" colspan="2">
								<button id="review" style="width: 100%; height: auto;"
									onclick="moveData('${cafeInfo.CF_NAME}','${cafeInfo.CF_NUMBER}')">후기작성</button>
							</td>
						</tr>
					</c:if>
					<c:if test="${empty sessionScope.user_id}">
						<tr>
							<td align="center" colspan="2">
								<button id="review" style="width: 100%; height: auto;" disabled>로그인이
									필요한 기능입니다!</button>
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
