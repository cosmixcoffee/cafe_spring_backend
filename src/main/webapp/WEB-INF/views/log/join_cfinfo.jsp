<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
$(function() {
    $("#showPostcode").click(function() {
        $("#cf_pcode, #cf_adr1, #cf_adr2").val(""); // 초기화
        new daum.Postcode({
            oncomplete: function(data) {
                let fullAddr = data.roadAddress || data.jibunAddress;
                let extraAddr = data.bname || "";

                if (data.buildingName) {
                    extraAddr += (extraAddr ? ", " + data.buildingName : data.buildingName);
                }

                fullAddr += (extraAddr ? " (" + extraAddr + ")" : "");
                $("#cf_pcode").val(data.zonecode);
                $("#cf_adr1").val(fullAddr);
                $("#cf_adr2").focus();
            }
        }).open();
    });
});
</script>

<div style="display: flex; justify-content: center; align-items: center; flex-direction: column;">
	<div id="title">
		<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; margin-top: 10px;">
			<div style="flex: 1; text-align: left;">
				<h2 id="titleMain" style="display: inline-block; margin: 0;">CAFE</h2>
			</div>
		</div>
		<form method="post" name="form1">
			<table border="1">
					<tr>
						<td style="width: 140px;">카페명</td>
						<td colspan="2" style="padding-left: 33px;"><input type="text" id="cf_name" ></td>
					</tr>
					<tr>
						<td>우편번호</td>
						<td style="width: 207px; padding-left: 33px;">
							<input type="text" id="cf_pcode"></td>
						<td style="width: 70px;">
							<input type="button" id="showPostcode" value="주소찾기">
						</td>
					</tr>	
					<tr>
						<td>주소</td>
						<td colspan="2" style="width: 207px; padding-left: 33px;">
						<input  style="width: 245px;" name="cf_adr1" id="cf_adr1"></td>
					</tr>
					<tr>
						<td>상세주소</td>
						<td colspan="2" style="width: 207px; padding-left: 33px;">
						<input style="width: 245px;" name="cf_adr2" id="cf_adr2"></td>
					</tr>
					<tr>
						<td>카페연락처</td>
						<td colspan="2" style="padding-left: 33px;"><input type="text" id="cf_tel"></td>
					</tr>
			</table>
		</form>
	</div>
</div>





