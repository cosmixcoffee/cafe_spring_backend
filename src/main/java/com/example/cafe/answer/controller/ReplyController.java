package com.example.cafe.answer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.cafe.answer.model.ReplyDAO;
import com.example.cafe.answer.model.ReplyDTO;
import com.example.cafe.answer.service.PageUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/reply/")
public class ReplyController {
	
	@Autowired
	ReplyDAO replyDao;
	
	@PostMapping("insert.do")
	public void insert(ReplyDTO dto, HttpSession session ) {
		String userid = (String) session.getAttribute("userid");
		dto.setReplyer(userid);
		replyDao.insert(dto);
	}
	
	@GetMapping("/delete/{idx}")
	public ResponseEntity<String> delete(@PathVariable(name = "idx") int idx) {
		ResponseEntity<String> entity = null;
		try {
			replyDao.delete(idx);
			entity = new ResponseEntity<>("success", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	
	@GetMapping("detail/{idx}")
	public ModelAndView detail(@PathVariable(name = "idx") int idx, ModelAndView mav) {
	    ReplyDTO dto = replyDao.detail(idx);
	    mav.setViewName("answer/reply_detail");
	    mav.addObject("dto", dto);
	    return mav;
	}
	
	@GetMapping("list.do")
	public ModelAndView list(@RequestParam(name= "board_idx") int board_idx,
			@RequestParam(name= "curPage", defaultValue = "1") int curPage, 
			ModelAndView mav,  HttpSession session) {
	    Integer userAuLv = (Integer) session.getAttribute("user_au_lv");
	    if (userAuLv == null) {
	        userAuLv = -1; // 기본값 설정 (null 방지)
	    }

		int count = replyDao.count(board_idx);
		PageUtil page_info = new PageUtil(count, curPage);
		int start = page_info.getPageBegin();
		int end = page_info.getPageEnd();
		
		
	    List<ReplyDTO> list;
	    if (userAuLv == 0) {
	        list = replyDao.adminList(board_idx, start, end);
	    } else if (userAuLv == 1) {
	        list = replyDao.managerList(board_idx, start, end);
	    } else {
	        list = replyDao.userList(board_idx, start, end);
	    }
	    
		mav.setViewName("answer/reply_list");
		mav.addObject("list", list);
		mav.addObject("page_info", page_info);
		
		System.out.println(end);
		System.out.println(start);
		System.out.println(board_idx);
		System.out.println(list);
		
		System.out.println(mav);
		return mav;
		
	}
	
	@GetMapping("list_json.do")
	public @ResponseBody List<ReplyDTO> list_json(@RequestParam(name= "idx") int idx,
			@RequestParam(name= "curPage", defaultValue = "1") int curPage, HttpSession session) {
		
		Integer userAuLv = (Integer) session.getAttribute("user_au_lv");
	    if (userAuLv == null) {
	        userAuLv = -1; // 기본값 설정 (null 방지)
	    }
		
		int count = replyDao.count(idx);
		PageUtil page_info = new PageUtil(count, curPage);
		int start = page_info.getPageBegin();
		int end = page_info.getPageEnd();

	    List<ReplyDTO> list;
	    if (userAuLv == 0) {
	        list = replyDao.adminList(idx, start, end);
	    } else if (userAuLv == 1) {
	        list = replyDao.managerList(idx, start, end);
	    } else {
	        list = replyDao.userList(idx, start, end);
	    }
	    
	    System.out.println(list);

		return list;
	}
	
	@PostMapping("/update/{idx}")
	public ResponseEntity<String> update(@PathVariable(name = "idx") int idx,
			@RequestBody ReplyDTO dto) {
		ResponseEntity<String> entity = null;
		try {
			dto.setIdx(idx);
			replyDao.update(dto);
			entity = new ResponseEntity<>("success", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
}
