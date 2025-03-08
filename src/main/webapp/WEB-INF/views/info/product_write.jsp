<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<%@ include file="../include/session_check.jsp" %>
<script src="/resources/ckeditor/ckeditor.js"></script>
<script>
$(document).ready(function () {
    $("#btnimgUpload").click(function () {
        let formData = new FormData();
        let mainImage = $("#mainImageInput")[0].files[0];
        let subImages = $("#subImageInput")[0].files;
        let cf_number = $("#cf_number").val();

        if (!mainImage) {
            alert("메인이미지를 선택하세요.");
            return;
        }

        formData.append("cf_number", cf_number);
        formData.append("mainImage", mainImage);
        Array.from(subImages).slice(0, 4).forEach((file, index) => {
            formData.append(`subImage${index + 1}`, file);
        });

        $.ajax({
            url: "/img_insert.do",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                alert(response.message || "이미지 저장 성공!");

                // 등록된 이미지로 미리보기 업데이트
                if (response.status === "success") {
                    if (response.mainImage) {
                        $("#preview").html('<img src="C:/upload/' + response.mainImage + '" style="max-width: 100%; max-height: 100%; display: block;">');
                    }

                    const previewBoxes = ["#subimage-box1", "#subimage-box2", "#subimage-box3", "#subimage-box4"];
                    response.subImages.forEach((path, index) => {
                        if (path) {
                            $(previewBoxes[index]).html('<img src="C:/upload/' + path + '" alt="서브 이미지" style="max-width: 100%; max-height: 100%; display: block;">');
                        }
                    });
                }
            },
            error: function (xhr, status, error) {
                alert("이미지 저장 실패: " + error);
            }
        });
    });
});
