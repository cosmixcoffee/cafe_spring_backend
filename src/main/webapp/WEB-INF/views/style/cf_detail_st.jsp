<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<style>
.container {
	width: 920px;
	height: 350px;
	height: auto;
	margin: 0 auto;
	padding: 20px;
	font-family: Arial, sans-serif;
}

.header {
	text-align: left;
	margin-bottom: 20px;
}

.image-section {
	display: flex;
	justify-content: space-between;
	margin-bottom: 10px;
	gap: 10px;
}

.main-image {
	width: 650px;
	height: 400px; /* 높이 증가 */
	display: flex;
	justify-content: center;
	overflow: hidden;
	box-sizing: border-box;
}

.main-image img {
	max-width: 100%;
	max-height: 100%;
	object-fit: cover;
}

.sub-images {
	display: grid;
	grid-template-columns: 1fr 1fr; /* 2열 */
	gap: 10px;
	max-height: 400px; /* 서브 이미지 전체 높이 = 200px * 2 */
	overflow: hidden;
	flex-shrink: 0;
}

.sub-image {
	width: 140px; /* 기존 140px → 2배 증가 */
	height: 200px; /* 기존 100px → 2배 증가 */
	display: flex;
	justify-content: center;
	overflow: hidden;
	box-sizing: border-box;
	border: 1px solid #ddd;
}

.sub-image img {
	max-width: 100%;
	max-height: 100%;
	object-fit: cover;
	cursor: pointer;
}

.info-section {
	margin-top: 20px;
	margin-bottom: 20px;
}

.info-row {
	display: flex;
	margin-bottom: 10px;
	align-items: center;
	text-align: left;
}

.label {
	font-weight: bold;
	margin-right: 10px;
}

.value {
	flex: 1;
	text-align: left;
}

</style>