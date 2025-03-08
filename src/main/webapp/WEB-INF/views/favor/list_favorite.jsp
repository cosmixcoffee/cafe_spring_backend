<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script src = "http://code.jquery.com/jquery-3.7.1.min.js"></script>
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
        
        if (isFavorite) {
            // 삭제 확인 메시지
            let confirmDelete = confirm("즐겨찾기를 삭제하시겠습니까?");
            if (!confirmDelete) {
                return;
            }
        }
        // Ajax 요청
        $.ajax({
            type: "get",
            url: "/add_del_favorite.do",
            data: params,
            success: function(response) {
                console.log("서버 응답: ", response);
				if (response === "deleted") {
                    alert("즐겨찾기에서 삭제되었습니다.");
                    $this.removeClass("active");
                    $.ajax({
                        type: "GET",
                        url: "/listFavor.do",
                        data:  {"userid" : userid},
                        success: function(updatedList) {
                            $("#resultDiv").html(updatedList);
                        },
                        error: function(xhr, status, error) {
                            console.error("메모 목록 갱신 실패:", error);
                            alert("메모 목록 갱신에 실패했습니다.");
                        }
                    });
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
<div style="display: flex; justify-content: center;">
    <div id="title">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;margin-top: 20px;">
            <div style="flex: 1; text-align: left; font-size: 12px;">
                <h2 id="titleMain" style="display: inline-block; margin: 0;">즐겨찾기한 카페</h2>
            </div>
        </div>
<div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
    ${userid} 님의 즐겨찾기 목록엔 ${count}개의 카페가 등록되어 있습니다.
    <!-- 카페 목록 출력 -->
    <table border="1" style="margin-top: 10px;">
        <thead>
            <tr>
                <th>즐겨찾기 번호</th>
                <th>유저ID</th>
                <th>카페명</th>
                <th>즐겨찾기</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${not empty list}">
                <c:forEach var="cafe" items="${list}">
                    <tr>
                        <td>${cafe.fav_idx}</td>
                        <td>${cafe.userid}</td>
                        <td>${cafe.cf_name}</td>
		                <td align="center">
							<!-- favoriteCafeIds에 포함 여부를 확인하여 active 클래스 설정 --> 
							<c:choose>
								<c:when test="${fn:contains(favoriteCafeIds, fav.cf_number)}">
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
                    </tr>
                </c:forEach>
            </c:if>
            <c:if test="${empty list}">
                <tr>
                    <td colspan="4">등록된 즐겨찾기가 없습니다.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
</div>
</div>

