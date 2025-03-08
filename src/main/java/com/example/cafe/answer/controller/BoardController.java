package com.example.cafe.answer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.cafe.answer.model.BoardDTO;
import com.example.cafe.answer.model.ReplyDAO;
import com.example.cafe.answer.service.BoardService;
import com.example.cafe.answer.service.PageUtil;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/board/*")
public class BoardController {

	@Autowired
	BoardService boardService;
	
	@Autowired
	ReplyDAO replyDao;
	
	@GetMapping("write.do")
	public String write() {
		return "answer/write";
	}
	
	@PostMapping("insert.do")
	public String insert(BoardDTO dto, HttpSession session) {
		String writer = (String) session.getAttribute("userid");
		dto.setWriter(writer);
		boardService.insert(dto);
		return "redirect:/board/list.do";
	}
	
	@RequestMapping("list.do")
	public ModelAndView list(@RequestParam(name= "curPage", defaultValue="1") int curPage,
			@RequestParam(name= "search_option", defaultValue="all") String search_option, 
			@RequestParam(name= "keyword", defaultValue="") String keyword,
			HttpSession session) {
	    
		Integer userAuLv = (Integer) session.getAttribute("user_au_lv");
	    if (userAuLv == null) {
	        userAuLv = -1; // 기본값 설정 (null 방지)
	    }
	    
	 // 먼저 count 값 계산
	    int count;
	    if (userAuLv == 0) {
	        count = boardService.adminCount(search_option, keyword);
	    } else if (userAuLv == 1) {
	        count = boardService.managerCount(search_option, keyword);
	    } else {
	        count = boardService.userCount(search_option, keyword);
	    }
		
	    // 페이징 객체 생성 후 start, end 계산
	    PageUtil page_info = new PageUtil(count, curPage);
	    int start = page_info.getPageBegin();
	    int end = page_info.getPageEnd();

	    // start, end가 결정된 후에 목록 조회
	    List<BoardDTO> list;
	    if (userAuLv == 0) {
	        list = boardService.adminList(start, end, search_option, keyword);
	    } else if (userAuLv == 1) {
	        list = boardService.managerList(start, end, search_option, keyword);
	    } else {
	        list = boardService.userList(start, end, search_option, keyword);
	    }
		
		System.out.println("user_au_lv: " + userAuLv);
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("answer/list");
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		map.put("search_option", search_option);
		map.put("keyword", keyword);
		map.put("page_info", page_info);
		map.put("user_au_lv", userAuLv);
		
		mav.addObject("map", map);
		
		/* System.out.println(map); */
		return mav;
	}
	
	@GetMapping("detail.do")
	public ModelAndView detail(@RequestParam(name= "idx") int idx,
			@RequestParam(name= "cur_page") int cur_page,
			@RequestParam(name= "search_option") String search_option,
			@RequestParam(name= "keyword") String keyword, HttpSession session) {
	    Integer userAuLv = (Integer) session.getAttribute("user_au_lv");
	    if (userAuLv == null) {
	        userAuLv = -1; // 기본값 설정 (null 방지)
	    }

	    BoardDTO dto;
	    if (userAuLv == 0) {
	        dto = boardService.adminDetail(idx);
	    } else if (userAuLv == 1) {
	        dto = boardService.managerDetail(idx);
	    } else {
	        dto = boardService.userDetail(idx);
	    }
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("answer/view");
		mav.addObject("dto", dto);
		mav.addObject("count", replyDao.count(idx));
		mav.addObject("cur_page", cur_page);
		mav.addObject("search_option", search_option);
		mav.addObject("keyword", keyword);
		
		return mav;
	}
	
	
	@PostMapping("update.do")
	public String update(BoardDTO dto, @RequestParam(value = "file_name", required = false) String file_name) {
	    System.out.println("업데이트 요청: " + dto);
	    boardService.update(dto);
	    
	    // ✅ 첨부파일이 있으면 업데이트
	    if (file_name != null && !file_name.isEmpty()) {
	        boardService.update_attach(file_name, dto.getIdx());
	    }
	
	    return "redirect:/board/list.do";
	}
	
	@PostMapping("delete.do")
	public String delete(@RequestParam(name= "idx") int idx) {
		boardService.delete(idx);
		return "redirect:/board/list.do";
	}
	
	@PostMapping("list_attach/{idx}")
	@ResponseBody
	public List<String> list_attach(@PathVariable(name= "idx") int idx) {
		return boardService.list_attach(idx);
	}
	
	
	
}
