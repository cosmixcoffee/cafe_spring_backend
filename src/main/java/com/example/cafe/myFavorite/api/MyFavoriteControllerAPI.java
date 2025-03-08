package com.example.cafe.myFavorite.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.cafe.myFavorite.model.MyFavoriteDAO;
import com.example.cafe.myFavorite.model.MyFavoriteDTO;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/favorite")
public class MyFavoriteControllerAPI {

	@Autowired
	MyFavoriteDAO myfavoriteDao;
	
	// 즐겨찾기 추가 삭제
	@GetMapping("/add_del_favorite.do")
	public Map<String, Object> addDelFavorite(@RequestParam(name = "userid", required = false) String userid,
			@RequestParam(name = "cf_number") int cf_number,
			HttpServletResponse res) throws IOException {
		
		int isFavorite = myfavoriteDao.checkFavorite(cf_number, userid);
		String status = "";
		
        if (isFavorite != 0) {
            status = myfavoriteDao.deleteFavorite(cf_number, userid);
            System.out.println("즐겨찾기가 삭제되었습니다!");
        } else {
        	MyFavoriteDTO dto = new MyFavoriteDTO();
            dto.setCf_number(cf_number);
            dto.setUserid(userid);
            status = myfavoriteDao.insertFavorite(dto);
            System.out.println("즐겨찾기가 추가되었습니다!");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("cf_number", cf_number);
        response.put("userid", userid);
        return response;
	}
	
	// 즐겨찾기 리스트
    @GetMapping("/listFavor.do")
    public Map<String, Object> listFavor(@RequestParam(name = "userid", required = false) String userid) {
        List<MyFavoriteDTO> items = myfavoriteDao.listMyFavorite(userid);
        
        //JSON형식으로 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("list", items);
        response.put("userid", userid);
        response.put("count", items.size());
        return response;
    }

    // 즐겨찾기 체크
    @GetMapping("/checkFavor.do")
    public ResponseEntity<Map<String, Object>> checkFavor(@RequestParam(name = "userid", required = false) String userid) {
        List<Integer> favoriteList = myfavoriteDao.getMyFavoriteId(userid); 
    	Map<String, Object> response = new HashMap<>();
    	response.put("favoriteCafeIds", favoriteList);
    	return ResponseEntity.ok(response);
    }

}
