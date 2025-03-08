package com.example.cafe.search.model;

import java.util.List;
import java.util.Map;

public interface CafeDAO {
	//검색 후 나온 카페 리스트
	List<CafeDTO> list_search(String searchkey, String search, int srart, int end);
	
	int count();
	
	//검색 후 나온 카페 수
	int search_count(String searchkey, String search);
	   
	
	// 마이페이지용 카페정보
	CafeDTO detailCafe(Integer cf_number, String userid);
	
	// 마이페이지 카페정보 수정
	void updateCafe(Map<String, Object> params);
	

	// 마이페이지 이미지등록 관련 _ 이미지 정보 
	CafeImageDTO img_info(int cf_number);
	// 마이페이지 이미지등록 관련 _ 이미지 삽입
	void img_insert(CafeImageDTO cafeiamgeDto);
	// 마이페이지 이미지등록 관련 _ 이미지 갱신할때 
	void img_update(CafeImageDTO cafeImageDto);
	// 마이페이지 이미지등록 관련 _ 이미지 체크시
	int img_count(int cf_number);
	// 마이페이지 이미지등록 관련 _ 이미지 갱신할때 기존이미지 삭제 
	void delete_images(int cf_number);
	
	// 카페정보 및 이미지
	Map<String, Object> detailCafeWithImages(int cf_number);
	
	boolean selectdelete(int cf_number);

}
