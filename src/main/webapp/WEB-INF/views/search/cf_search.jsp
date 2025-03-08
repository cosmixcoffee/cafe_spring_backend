<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
$(function () {
	let userid = "${sessionScope.userid}";
	// 페이지 로드 시 Ajax로 리스트 요청
	if(userid){
		$.ajax({
	        type: "get", // 혹은 "post" (요구 사항에 따라 선택)
	        url: "/checkFavor.do", // 요청할 URL
	        data: {"userid" : userid },
	        success: function(favoriteCafeIds) {
	            // 응답받은 리스트를 콘솔에 출력
	            console.log("즐겨찾기 리스트:", favoriteCafeIds);

	            // 필요한 경우 리스트를 JSP의 데이터에 활용할 수 있음
	            //각 버튼 업데이트 datacfnumber에 담긴 cfNUmber가 리스트안에 들어있다면 class를 active로 설정-> 색 바뀜
	            $(".addFavorite").each(function() {
	                let $this = $(this);
	                let cf_number = $this.data("cf_number");
	                console.log("현재 버튼 cf_number:", cf_number);
	                if (favoriteCafeIds.includes(Number(cf_number))) {
	                    $this.addClass("active");
	                }
	            });
	        },
	        error: function(xhr, status, error) {
	            console.error("리스트 요청 실패:", error);
	        }
	    });			
	} else{
		console.log("userid가 null입니다.")
	}

	 // 즐겨찾기 추가/삭제 처리
    $(".addFavorite").click(function() {
        // 현재 클릭한 버튼을 참조
        let $this = $(this);
        // data-cfnumber 속성을 이용하여 카페 번호 가져오기
        let cf_number = $this.attr('data-cf_number');
        //로그인 되어있는 아이디
        let userid = $this.attr('data-userid');
        // 버튼의 현재 상태 확인 (active 클래스 존재 여부)
        //true false로 반환한다 active인지 아닌지
        let isFavorite = $this.hasClass("active");
        // 서버로 전송할 데이터 설정
        let params = { "cf_number": cf_number , "userid" : userid};
		
        console.log(params);
        // Ajax 요청
        $.ajax({
            type: "get",
            url: "/add_del_favorite.do",
            data: params,
            success: function(response) {
                console.log("서버 응답: ", response);
                if (response === "added") {
                    alert("즐겨찾기에 추가되었습니다.");
                    // active 클래스 추가 (노란색으로 표시)
                    $this.addClass("active");
                } else if (response === "deleted") {
                    alert("즐겨찾기에서 삭제되었습니다.");
                    // active 클래스 제거 (회색으로 표시)
                    $this.removeClass("active");
                } else {
                    alert("알 수 없는 서버 응답: " + response);
                }
            },
            error: function(xhr, status, error) {
                alert("즐겨찾기 처리에 실패했습니다.");
                console.error("Ajax 요청 실패:", error);
            }
        });
    });	
	
    $(".addMemo").click(function () {
        let cf_number = $(this).data("cf_number"); 
        let cf_name = $(this).data("cf_name");   
        let userid = $(this).data("userid");    

        console.log("📌 이동할 때 값:", cf_number, cf_name, userid); // 콘솔에서 값 확인

        // AJAX 대신 페이지 이동으로 처리
        window.location.href = "/listMemo.do?cf_number=" + encodeURIComponent(cf_number) +
                                 "&cf_name=" + encodeURIComponent(cf_name) +
                                 "&userid=" + encodeURIComponent(userid);
    });
    
    // 전체 선택/해제
    $("#checkAll").click(function() {
        $(".delete-checkbox").prop("checked", this.checked);
    });
    
    
});
</script>

<style>
/* 기본 별 색상 (활성화되지 않았을 때) */
.addFavorite {
    fill: #ddd !important; /* 회색 */
    cursor: pointer;
}

/* 활성화된 별 색상 (즐겨찾기 추가된 상태) */
.addFavorite.active {
    fill: #ffcc00 !important; /* 노란색 */
}
</style>

<div style="margin-bottom: 10px">${count}개의 카페가 검색되었습니다.</div>

<table border="1">
    <thead>
        <tr>
            <th>No</th>
            <th>카페명</th>
            
            <%-- 카페코드  특정 회원에게만 표시 --%>
            <c:if test="${sessionScope.user_au_lv == '0' || sessionScope.user_au_lv == '1'}">
            	<th>카페코드</th>
    		</c:if>
            
            <th>주소</th>
            <th>연락처</th>
            <th>내용</th>
            <th>리뷰수</th>
            <th>카페평점</th>
            <th>즐겨찾기</th>
            <th>메모</th>
            <th>
	            <c:if test="${sessionScope.user_au_lv == 0}">
	           		<input type="checkbox" id="checkAll">  
	            </c:if>
	        </th>
        </tr>
    </thead>
    <tbody>
    	<c:if test="${not empty list}">
	        <c:forEach var="cafe" items="${list}">
	      
	            <tr>
	      
	                <td align="center">
	                <a href= "/cafe_detail.do?cf_number=${cafe.cf_number}" style="color: black; text-decoration: none;">
	                ${cafe.cf_number}
	                </a></td>
	                <td align="center">
	                <a href= "/cafe_detail.do?cf_number=${cafe.cf_number}" style="color: black; text-decoration: none;">
	                ${cafe.cf_name}
	                </a></td>
	           
	                <c:if test="${sessionScope.user_au_lv == '0' || sessionScope.user_au_lv == '1'}">
                		<td align="center">${cafe.cf_code}</td> 
                	</c:if>
	                
	                <td align="center">${cafe.cf_adr1}</td>
	                <td align="center">${cafe.cf_tel}</td>
	                <td align="center">${cafe.cf_content}</td>
	                <td align="center">${cafe.cf_rv_count}</td>
	                <td align="center">${cafe.cf_point}</td>
	                <c:if test="${ not empty sessionScope.userid}">
		                <td align="center">
								<!-- favoriteCafeIds에 포함 여부를 확인하여 active 클래스 설정 --> 
								<c:choose>
									<c:when test="${fn:contains(favoriteCafeIds, cafe.cf_number)}">
										<!-- favoriteCafeIds에 포함된 경우 active 클래스를 추가 -->
										<svg class="addFavorite active"
											data-cf_number="${cafe.cf_number}"
											data-userid="${sessionScope.userid}"
											xmlns="http://www.w3.org/2000/svg" width="16" height="16"
											fill="currentColor" viewBox="0 0 16 16">
	                                    <path
												d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
	                                </svg>
									</c:when>
									<c:otherwise>
										<!-- favoriteCafeIds에 포함되지 않은 경우 active 클래스 없음 -->
										<svg class="addFavorite" data-cf_number="${cafe.cf_number}"
											data-userid="${sessionScope.userid}"
											xmlns="http://www.w3.org/2000/svg" width="16" height="16"
											fill="currentColor" viewBox="0 0 16 16">
	                                    <path
												d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
	                                </svg>
									</c:otherwise>
								</c:choose>
						</td>
	                	<td>
	                		<input type="button" class="addMemo" data-cf_number="${cafe.cf_number}" data-cf_name="${cafe.cf_name}" data-userid = "${sessionScope.userid}" value="메모">
						</td>
	                </c:if>
	                <c:if test="${empty sessionScope.userid}">
						<td colspan="2" align="center">회원 전용</td>
					</c:if>
					<td>
			            <c:if test="${sessionScope.user_au_lv == 0}">
			                <input type="checkbox" class="delete-checkbox" value="${cafe.cf_number}">
			            </c:if>
			        </td>
	            </tr>
	        </c:forEach>
	    </c:if>
        <c:if test="${empty list}">
            <tr>
                <td colspan="11" align="center">등록된 카페가 없습니다.</td>
            </tr>
        </c:if>
        <tr align="center">
			<td colspan="11">
				<c:if test="${page.curPage > 1}">
					<a href="#" onclick="list('1')" >[처음]</a>
				</c:if>
				<c:if test="${page.curBlock > 1}">
					<a href="#" onclick="list('${page.prevPage}')">[이전]</a>
				</c:if>
				
				<c:forEach var="num" begin="${page.blockStart}" end = "${page.blockEnd}">
					<c:choose>
						<c:when test="${num == page.curPage}">
							<span style="color:black">${num}</span>
						</c:when>
						<c:otherwise>
							<a href="#" onclick="list('${num}')">${num}</a>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${page.curBlock < page.totBlock}">
					<a href="#" onclick="list('${page.nextPage}')">[다음]</a>
				</c:if>
				<c:if test="${page.curPage < page.totPage}">
					<a href="#" onclick="list('${page.totPage}')">[마지막]</a>
				</c:if>
			</td>
		</tr>
    </tbody>
</table>