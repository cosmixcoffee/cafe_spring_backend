package com.example.cafe.myFavorite.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cafe.myFavorite.model.MyFavoriteDAO;
import com.example.cafe.myFavorite.model.MyFavoriteDTO;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MyFavoriteController {

	@Autowired
	MyFavoriteDAO myfavoriteDao;
	
	// 즐겨찾기 추가 삭제
	@GetMapping("add_del_favorite.do")
	public void addDelFavorite(@RequestParam(name = "userid", required = false) String userid,
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
        res.setContentType("text/plain");
        res.getWriter().write(status);
    }
	
	// 즐겨찾기 리스트
    @GetMapping("listFavor.do")
    public String listFavor(@RequestParam(name = "userid", required = false) String userid,
    		Model model) {
        List<MyFavoriteDTO> items = myfavoriteDao.listMyFavorite(userid);
        model.addAttribute("list", items);
        model.addAttribute("userid", userid);
        model.addAttribute("count", items.size());
        return "favor/list_favorite";  // JSP 뷰 이름
    }

    // 즐겨찾기 체크
    @GetMapping("checkFavor.do")
    @ResponseBody
    public List<Integer> checkFavor(@RequestParam(name = "userid", required = false) String userid) {
        return myfavoriteDao.getMyFavoriteId(userid);
    }

}
