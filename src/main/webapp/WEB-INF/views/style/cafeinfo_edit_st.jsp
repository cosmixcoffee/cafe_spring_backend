<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<style>
/* 메인이미지 + 서브이미지 배치를 한 줄로 정렬 */
.main-section {
    display: flex;
    flex-direction: row; /* 가로 배치 */
    justify-content: space-between;
    align-items: center;
    width: 920px; /* 전체 너비 (600px + 300px + 20px 간격) */
    height: 350px; /* 메인이미지와 서브이미지 높이를 동일하게 설정 */
    margin-bottom: 30px; /* 메인이미지 & 서브이미지 아래 간격 추가 */
}

/* 메인이미지 박스 */
.image-box {
    width: 600px; /* 고정된 너비 설정 */
    height: 350px;
    display: flex;
    flex-direction: column; /* 이미지와 버튼을 세로 정렬 */
    align-items: center;
    justify-content: space-between;
    overflow: hidden;
    box-sizing: border-box;
    margin-right: 20px; /* 서브이미지와의 간격 */
}

/* 미리보기 박스 (메인이미지) */
.preview-box {
    width: 100%;
    height: 310px; /* 버튼 높이(40px)를 제외한 정확한 높이 설정 */
    display: flex;
    justify-content: center;
    align-items: center;
    border-bottom: 1px solid #000;
    border: 1px solid #ddd;
}

/* 메인이미지 스타일 */
.preview-box img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* 메인이미지 파일 미리보기 버튼 */
.mainimage-button-box {
    width: 100%;
    height: 40px; /* 버튼 높이 고정 */
    display: flex;
    justify-content: center;
    align-items: center;
}

/* 서브이미지 컨테이너 */
.subimage-section {
    display: flex;
    flex-direction: column; /* 이미지 + 버튼을 세로로 정렬 */
    align-items: center;
    width: 300px; /* 서브이미지 컨테이너 너비 */
    height: 350px; /* 메인이미지 높이와 동일하게 설정 */
    justify-content: space-between;
}

/* 서브이미지를 2x2 그리드로 배치 */
.subimage-grid {
    display: grid;
    grid-template-columns: 1fr 1fr; /* 2열 */
    grid-template-rows: 1fr 1fr; /* 2행 */
    gap: 10px;
    width: 100%;
    height: 310px; /* 버튼 높이(40px)를 제외한 정확한 높이 설정 */
    align-items: center;
    justify-content: center;
}

/* 서브이미지 개별 박스 */
.subimage-box {
    width: 140px;
    height: 150px; /* 2개의 서브이미지가 310px 내에서 균등 분배되도록 조정 */
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    box-sizing: border-box;
    border: 1px solid #ddd;
}

/* 서브이미지 스타일 */
.subimage-box img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    cursor: pointer;
}

/* 서브이미지 버튼 박스 - 서브이미지 내부에서 아래에 배치 */
.subimage-button-box {
    width: 100%;
    height: 40px; /* 버튼 높이 고정 */
    display: flex;
    justify-content: center;
    align-items: center;
}

/* 파일 미리보기 버튼 스타일 */
#mainImageButton,
#subImageButton {
    font-size: 15px;
    cursor: pointer;
    border: 1px solid #000;
    background-color: #f0f0f0;
    color: #000;
    text-align: center;
    box-sizing: border-box;
    padding: 5px 10px;
    border-radius: 5px;
}

#mainImageButton:hover,
#subImageButton:hover {
    background-color: #e0e0e0;
}

/* 텍스트 박스 (메인이미지 & 서브이미지 아래 공간 확보) */
.text-box {
    width: 920px;

    padding: 10px;
    height: auto;
    box-sizing: border-box;
    position: relative;
    margin-top: 10px; /* 위쪽 여백을 추가하여 메인 이미지와 떨어지도록 설정 */
}

/* 텍스트 행을 가로로 정렬 */
.text-row {
    display: flex;
    align-items: center; /* 수직 중앙 정렬 */
    justify-content: flex-start; /* 왼쪽 정렬 */
    width: 100%;
    margin-bottom: 10px; /* 행 간격 추가 */
}

/* 텍스트 라벨 (카페 이름) */
.text-label {
    font-weight: bold;
    width: 120px; /* 라벨 너비 조정 */
    text-align: left; /* 텍스트 왼쪽 정렬 */
    margin-right: 10px; /* 라벨과 값 사이 간격 */
}

/* 텍스트 값 */
.text-value {
    flex: 1; /* 남은 공간을 차지하도록 설정 */
    text-align: left; /* 왼쪽 정렬 */
}


</style>