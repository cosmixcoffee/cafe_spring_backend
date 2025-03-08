package com.example.cafe.search.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class CafeDAOImpl implements CafeDAO {
	
	@Autowired
	SqlSession session;
	
	// 카페 검색리스트
	@Override
	public List<CafeDTO> list_search(String searchkey, String search, int start, int end) {
		// TODO Auto-generated method stub
		List<CafeDTO> list = null;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("searchkey", searchkey);
			map.put("search", search);
			map.put("start", start);
			map.put("end", end);
			list = session.selectList("cafesearch.search_list", map);
			for (CafeDTO dto : list) {
				String cf_name = dto.getCf_name();
				String cf_adr1 = dto.getCf_adr1();
				String cf_adr2 = dto.getCf_adr2();
				
				switch (searchkey) {
				case "all":
					cf_name = cf_name.replace("search", search);
					cf_adr1 = cf_adr1.replace("search", search);
				case "cf_name": 
					cf_name = cf_name.replace(search,search );
					break;
				case "cf_adr1": 
					cf_adr1 = cf_adr1.replace(search,search);
					break;
				}
				dto.setCf_name(cf_name);
				dto.setCf_adr1(cf_adr1);
				dto.setCf_adr2(cf_adr2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	// 카페 총 갯수 
	@Override
	public int count() {
		int result = 0;
		try {
			result = session.selectOne("cafesearch.count");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return result;
	}
	
	// 카페 검색 후 나온 갯수
	@Override
	public int search_count(String searchkey, String search) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("searchkey", searchkey);
			map.put("search", search);
			result = session.selectOne("cafesearch.search_count", map);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
			
	// 마이페이지용 카페정보
	@Override
	public CafeDTO detailCafe(Integer cf_number, String userid) {
		CafeDTO dto = null;
	    try {
	        // 검색 조건을 담을 Map 생성
	        Map<String, Object> params = new HashMap<>();
	        params.put("cf_number", cf_number); // CF_NUMBER가 없으면 null
	        params.put("userid", userid); // ID가 없으면 null
	        
	        dto = session.selectOne("cafesearch.detail_cafe", params);

	        if (dto != null) {
	            System.out.println("DAO: detailCafe 반환된 DTO - cf_number: " + dto.getCf_number() + ", userid: " + dto.getUserid());
	        } else {
	            System.out.println("DAO: detailCafe 반환된 DTO가 null입니다.");
	        }
	    } catch (Exception e) {
	        System.err.println("DAO: detailCafe 호출 중 오류 발생 - " + e.getMessage());
	    }
		return dto;
	}
	
    //마이페이지 카페정보수정
    @Transactional
    @Override
    public void updateCafe(Map<String, Object> params) {
        session.update("cafesearch.updateCafe", params);

    }
	
	// 마이페이지 관려 카페이미지 등록관련 코드
	@Override
	public CafeImageDTO img_info(int cf_number) {
		return session.selectOne("cafesearch.img_info", cf_number);
	}
	

	@Override
	public void img_insert(CafeImageDTO cafeiamgeDto) {
		session.insert("cafesearch.img_insert", cafeiamgeDto);
	}
	
    @Override
    public void img_update(CafeImageDTO cafeImageDto) {
        session.update("cafesearch.img_update", cafeImageDto);
    }
       
    
    @Override
    public int img_count(int cf_number) {
        return session.selectOne("cafesearch.img_count", cf_number);
    }
    
    @Override
    public void delete_images(int cf_number) {
    	session.delete("cafesearch.deleteImages", cf_number);
    }
    
	// 카페정보 및 이미지
	@Override
    public Map<String, Object> detailCafeWithImages(int cf_number) {
        // 카페 기본 정보 조회
        CafeDTO cafeInfo = session.selectOne("cafesearch.detailCafe", cf_number);
        // 카페 이미지 정보 조회
        CafeImageDTO cafeImages = session.selectOne("cafesearch.getImages", cf_number);

        // 결과를 Map에 저장
        Map<String, Object> result = new HashMap<>();
        result.put("cafeInfo", cafeInfo);
        result.put("cafeImages", cafeImages);

        return result;
    }
	
	@Override
	public boolean selectdelete(int cf_number) {
	    try {
	        int result = session.delete("cafesearch.selectdelete", cf_number);
	        return result > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
   
}
