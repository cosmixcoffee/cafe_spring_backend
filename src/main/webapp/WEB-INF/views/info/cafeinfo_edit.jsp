<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(document).ready(function () {
    console.log("jQuery 로드 완료");
    // 페이지 로드 시 카페 정보 가져오기
    $.ajax({
        type: "GET",
        url: "/cafe_detailcheck.do?userid=${sessionScope.userid}&cf_number=${sessionScope.cf_number}",
        success: function (response) {
            console.log("서버 응답:", response); // 서버 응답 확인

            if (response.status === "success") {
                // CF_NUMBER 설정
   				 let cf_number = "${sessionScope.cf_number}";
                $("#cf_number").val(cf_number);
                // JSON 데이터 사용
                $("#userid").text(response.userid);
                $("#cf_name").text(response.cf_name);
                $("#cf_adr1").text(response.cf_adr1);
                $("#cf_adr2").text(response.cf_adr2);
                $("#cf_tel").val(response.cf_tel);
                $("#cf_content").val(response.cf_content);
                $("#cf_map").val(response.cf_map);
                $("#cf_visit_num").val(response.cf_visit_num);
                $("#cf_rv_count").val(response.cf_rv_count);
                $("#cf_point").text(response.cf_point);
                loadCafeImages(cf_number);
            } else if (response.status === "nonumber") {
                alert(response.message); // "카페를 등록해주세요."
                
                if (response.redirect) {
                    window.location.href = response.redirect; // 페이지 이동
                }
            } else {
                alert(response.message || "카페 정보를 가져오는 중 문제가 발생했습니다.");
                
            }
        },
        error: function (xhr, status, error) {
            console.error("AJAX 요청 오류:", status, error);
            alert("카페 정보를 불러오는 중 오류가 발생했습니다.");
        }
    });

    function loadCafeImages(cf_number) {
        $.ajax({
            type: "GET",
            url: "/getImages.do",
            data: { cf_number: cf_number },
            success: function (response) {
                console.log("📌 이미지 서버 응답:", response);

                if (response.status === "success") {
                    // 메인이미지
                    if (response.data.main_img_path) {
                        $("#preview").html('<img src="' + response.data.main_img_path + '" style="max-width: 100%; max-height: 100%; display: block;">');
                    } else {
                        $("#preview").html("미리보기 없음");
                    }
                    // 서브이미지
                    const previewBoxes = ["#subimage-box1", "#subimage-box2", "#subimage-box3", "#subimage-box4"];
                    const subImagePaths = [
                        response.data.sub_img1_path, 
                        response.data.sub_img2_path, 
                        response.data.sub_img3_path, 
                        response.data.sub_img4_path
                    ];

                    previewBoxes.forEach((boxId, index) => {
                        const imagePath = subImagePaths[index];
                        if (imagePath) {
                            $(boxId).html('<img src="' + imagePath + '" style="max-width: 100%; max-height: 100%; display: block;">');
                        } else {
                            $(boxId).html("미리보기 없음");
                        }
                    });
                } else {
                    alert(response.message || "이미지가 없습니다.");
                }
            },
            error: function (xhr, status, error) {
                console.error("❌ 이미지 AJAX 요청 오류:", status, error);
                alert("이미지 데이터를 불러오는 중 오류가 발생했습니다.");
            }
        });
    }
 
    // 메인이미지 버튼 클릭 시 파일 선택 창 열기
    $("#mainImageButton").click(function () {
        $("#mainImageInput").click();
    });

    // 메인이미지 선택 시 미리보기 표시
    $("#mainImageInput").change(function () {
        const file = this.files[0];
        const previewBox = $("#preview");
        previewBox.html("");

        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewBox.html('<img src="' + e.target.result + '" style="max-width: 100%; max-height: 100%; display: block;">');
            };
            reader.readAsDataURL(file);
        }
    });

    // 서브이미지 버튼 클릭 시 파일 선택 창 열기
    $("#subImageButton").click(function () {
        $("#subImageInput").click();
    });


    // 서브이미지 선택 시 미리보기 표시
    $("#subImageInput").change(function () {
        const files = this.files;
        const maxFiles = 4;
        const previewBoxes = ["#subimage-box1", "#subimage-box2", "#subimage-box3", "#subimage-box4"];

        previewBoxes.forEach(boxId => $(boxId).html("미리보기 없음"));

        Array.from(files).slice(0, maxFiles).forEach((file, index) => {
            const reader = new FileReader();
            const previewBox = $(previewBoxes[index]);

            reader.onload = function (e) {
                previewBox.html('<img src="' + e.target.result + '" style="max-width: 100%; max-height: 100%; display: block;">');
            };
            reader.readAsDataURL(file);
        });
    });

    
 // 업로드 버튼 클릭 시
    $("#btnimgUpload").click(function () {
        let formData = new FormData();
        let mainImage = $("#mainImageInput")[0].files[0];
        let cf_number = $("#cf_number").val();

        if (!mainImage) {
            alert("메인이미지를 선택하세요.");
            return;
        }

        formData.append("cf_number", cf_number);
        formData.append("mainImage", mainImage);

        // 서브이미지를 개별적으로 추가
        let subImages = $("#subImageInput")[0].files;
        if (subImages.length > 0) formData.append("subImage1", subImages[0] || "");
        if (subImages.length > 1) formData.append("subImage2", subImages[1] || "");
        if (subImages.length > 2) formData.append("subImage3", subImages[2] || "");
        if (subImages.length > 3) formData.append("subImage4", subImages[3] || "");

        // FormData 내부 확인 로그 추가
        console.log("업로드 요청 데이터 확인:");
        for (let pair of formData.entries()) {
            console.log(pair[0] + ": ", pair[1]);
        }

        $.ajax({
            url: "/img_insert.do",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log("이미지 업로드 성공 응답:", response);

                alert(response.message || "이미지 저장 성공!");

                // 등록된 이미지로 미리보기 업데이트
                if (response.status === "success") {
                    if (response.mainImage) {
                        console.log("저장된 이미지 파일명:", response.mainImage, response.subFileNames);
                        $("#preview").html('<img src="/resources/images/' + response.mainImage + '" style="max-width: 100%; max-height: 100%; display: block;">');
                    }

                    const previewBoxes = ["#subimage-box1", "#subimage-box2", "#subimage-box3", "#subimage-box4"];
                    response.subFileNames.forEach((path, index) => {
                        if (path) {
                            $(previewBoxes[index]).html('<img src="/resources/images/' + path + '" alt="서브 이미지" style="max-width: 100%; max-height: 100%; display: block;">');
                        }
                    });
                }
            },
            error: function (xhr, status, error) {
                alert("이미지 저장 실패: " + error);
            }
        });
    });

    // 정보 수정 버튼 클릭 이벤트
    $("#cfupBtn").click(function () {
    	
    	
			
        
		let cf_number = "${sessionScope.cf_number}";
     
        if (!cf_number || cf_number.trim() === "" || isNaN(cf_number)) {
            alert("cf_number를 확인할 수 없습니다. 페이지를 새로고침하고 다시 시도하세요.");
            return; // CF_NUMBER가 없으면 함수 종료
        }
        
        cf_number = parseInt(cf_number.trim(),10);

        const params = {
        	cf_number: isNaN(cf_number) ? 0 : cf_number,
            cf_name: $("#cf_name").val() ? $("#cf_name").val().trim() : null,
            cf_code: $("#cf_code").val() ? $("#cf_code").val().trim() : null,
            cf_pcode: $("#cf_pcode").val() ? $("#cf_pcode").val().trim() : null,
            cf_adr1: $("#cf_adr1").val() ? $("#cf_adr1").val().trim() : null,
            cf_adr2: $("#cf_adr2").val() ? $("#cf_adr2").val().trim() : null,
            cf_tel: $("#cf_tel").val() ? $("#cf_tel").val().trim() : null,
            cf_content: $("#cf_content").val() ? $("#cf_content").val().trim() : null
        };
        
        console.log("params:", params);

        $.ajax({
            type: "POST",
            url: "/cafeinfoup.do?cf_number=${sessionScope.cf_number}",
            data: params,
            success: function (response) {
            	
                alert(response.message || "카페 정보 업데이트 성공!");
                if (response.status === "success") {
                	// location.reload(); 성공 시 페이지 새로고침
                	$.ajax({
                		 type: "get",
                		 url: "/cafe_detailcheck.do?userid=${sessionScope.userid}&cf_number=${sessionScope.cf_number}",
                		 success: function(response){
                			 console.log("서버 응답:", response);
                			 if (response.status === "success") {
                	                // CF_NUMBER 설정
                	   				 let cf_number = "${sessionScope.cf_number}";
                	                $("#cf_number").val(cf_number);

                	                // JSON 데이터 사용
                	                $("#userid").text(response.userid);
                	                $("#cf_name").text(response.cf_name);
                	                $("#cf_adr1").text(response.cf_adr1);
                	                $("#cf_adr2").text(response.cf_adr2);
                	                $("#cf_tel").val(response.cf_tel);
                	                $("#cf_content").val(response.cf_content);
                	                $("#cf_map").val(response.cf_map);
                	                $("#cf_visit_num").val(response.cf_visit_num);
                	                $("#cf_rv_count").val(response.cf_rv_count);
                	                $("#cf_point").text(response.cf_point);

                	                loadCafeImages(cf_number); 
                	            } else {
                	                alert(response.message || "카페 정보를 가져오는 중 문제가 발생했습니다.");
                	            }
                	        },
                	        error: function (xhr, status, error) {
                	            console.error("AJAX 요청 오류:", status, error);
                	            alert("카페 정보를 불러오는 중 오류가 발생했습니다.");
                	        }
                	    });
                }
            },
            error: function () {
            	 console.log("params:", params);
                alert("카페 정보 업데이트 중 오류가 발생했습니다.");
            }
        });
    });
});
</script>
<%@ include file="../style/cafeinfo_edit_st.jsp"%>
</head>
<body>
	<div style="display: flex; justify-content: center;">
		<input type="hidden" id="cf_number" value="">
		<div id="title">
			<div
				style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; margin-top: 20px;">
				<div style="flex: 1; text-align: left; font-size: 12px;">
					<h2 id="titleMain" style="display: inline-block; margin: 0;">카페정보수정</h2>
				</div>
			</div>
			<form>
				<div class="main-content">
					<!-- 메인이미지와 텍스트 -->
					<div class="main-section">
						<!-- 메인이미지 -->
						<div id="mainimage" class="image-box">
							<!-- 1행: 미리보기 영역 -->
							<div id="preview" class="preview-box">메인이미지</div>
							<!-- 2행: 버튼 영역 -->
							<div id="upload-button-box" class="button-box">
								<button type="button" id="mainImageButton">파일 미리보기</button>
								<input type="file" id="mainImageInput" accept="images/*"
									style="display: none;">
							</div>
						</div>




						<!-- 서브이미지 -->
						<div id="subimage" class="subimage-section">
						    <!-- 2x2 서브이미지 미리보기 -->
						    <div class="subimage-grid">
						        <div class="subimage-box" id="subimage-box1">서브이미지1</div>
						        <div class="subimage-box" id="subimage-box2">서브이미지2</div>
						        <div class="subimage-box" id="subimage-box3">서브이미지3</div>
						        <div class="subimage-box" id="subimage-box4">서브이미지4</div>
						    </div>
						    
						    <!-- 서브이미지 파일 미리보기 버튼 (아래 배치) -->
						    <div id="upload-button-box" class="subimage-button-box">
						        <button type="button" id="subImageButton">파일 미리보기</button>
						        <input type="file" id="subImageInput" accept="image/*" multiple style="display: none;">
						    </div>
						</div>
					</div>
	
					<div id="text" class="text-box">
						<div class="text-row">
							<div class="text-label">카페 이름</div>
							<div class="text-value">
								<span id="cf_name"></span>
							</div>
						</div>
						<div class="text-row">
							<div class="text-label">담당자</div>
							<div class="text-value">
								<span id="userid"></span>
							</div>
						</div>
						<div class="text-row">
							<div class="text-label">주소</div>
							<div class="text-value">
								<span id="cf_adr1"></span> <span id="cf_adr2"></span>
							</div>
						</div>
						<div class="text-row">
							<div class="text-label">연락처</div>
							<div class="text-value">
								<input type="text" id="cf_tel" class="text-input-small">
							</div>
						</div>
						<div class="text-row">
							<div class="text-label">카페 소개문</div>
							<div class="text-value">
								<textarea id="cf_content" rows="5"
									style="resize: none; width: 95%;"></textarea>
							</div>
						</div>
					</div>
				</div>
				<div align="center" style="margin-top: 10px;">
					<button type="button" id="btnimgUpload">이미지 등록</button>
					<button type="button" id="cfupBtn">정보 수정</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>