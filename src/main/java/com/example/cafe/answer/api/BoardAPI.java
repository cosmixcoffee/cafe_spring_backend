package com.example.cafe.answer.api;

import com.example.cafe.answer.model.BoardDAO;
import com.example.cafe.answer.model.BoardDTO;
import com.example.cafe.answer.model.ReplyDAO;
import com.example.cafe.answer.service.BoardService;
import com.example.cafe.answer.service.PageUtil;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/board/*")
public class BoardAPI {
    
	private static final Logger logger = LoggerFactory.getLogger(BoardAPI.class);

    
    @Autowired
    private BoardService boardService;
    
    @Autowired
    private ReplyDAO replyDao;
    
    @Autowired
    private BoardDAO boardDao;

    @GetMapping("list")
    public ResponseEntity<Map<String, Object>> getSuggestions(
            @RequestParam(name = "curPage", defaultValue = "1") int curPage,
            @RequestParam(name = "search_option", defaultValue = "all") String search_option,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @CookieValue(value = "user_au_lv", required = false) Integer user_au_lv,
            HttpSession session) {

        Integer userAuLv = (Integer) session.getAttribute("user_au_lv");
        

        System.out.println(user_au_lv);
        
        logger.info("📌 사용자 권한 등급: user_au_lv = " + user_au_lv);

        try {
            // 📌 권한별 게시글 개수 조회
            int count = switch (user_au_lv) {
                case 0 -> boardService.adminCount(search_option, keyword);
                case 1 -> boardService.managerCount(search_option, keyword);
                default -> boardService.userCount(search_option, keyword);
            };

            // 📌 페이징 정보 설정
            PageUtil page_info = new PageUtil(count, curPage);
            int start = page_info.getPageBegin();
            int end = page_info.getPageEnd();

            // 📌 권한별 게시글 목록 조회
            List<BoardDTO> list = switch (user_au_lv) {
                case 0 -> boardService.adminList(start, end, search_option, keyword);
                case 1 -> boardService.managerList(start, end, search_option, keyword);
                default -> boardService.userList(start, end, search_option, keyword);
            };

            // 📌 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("list", list);
            response.put("count", count);
            response.put("search_option", search_option);
            response.put("keyword", keyword);
            response.put("page_info", page_info);
            response.put("user_au_lv", user_au_lv);

            // ✅ JSON 형식 응답 강제 설정
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (Exception e) {
            logger.error("❌ 게시글 조회 중 오류 발생", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "게시글을 불러올 수 없습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }



    // 건의사항 작성
    @PostMapping("/write")
    public ResponseEntity<String> createSuggestion(@RequestBody BoardDTO boardDTO) {
        if (boardDTO.getWriter() == null || boardDTO.getWriter().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("작성자 정보가 없습니다.");
        }

        boardService.insert(boardDTO);
        return new ResponseEntity<>("글 작성 성공", HttpStatus.CREATED);
    }


    // 건의사항 상세 조회
 // ✅ 기존 "view.jsp"를 반환하는 ModelAndView를 JSON 응답으로 변환
    @GetMapping("view")
    public ResponseEntity<?> getBoardDetail(
            @RequestParam(name= "idx") int idx,
            @RequestParam(name= "cur_page", required = false) Integer curPage,
            @RequestParam(name= "search_option", required = false) String searchOption,
            @RequestParam(name= "keyword", required = false) String keyword,
            HttpSession session) {

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

        if (dto == null) {
            return new ResponseEntity<>("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("dto", dto);
        response.put("count", replyDao.count(idx)); // 댓글 개수 포함

        return ResponseEntity.ok(response);
    }



    // 건의사항 수정
    @PostMapping("update.do")
    @ResponseBody
    public ResponseEntity<String> update(
            @RequestBody BoardDTO dto,
            @RequestParam(value = "file_name", required = false) String file_name) {
        
        System.out.println("🔥 업데이트 요청: " + dto);
        boardService.update(dto);

        // ✅ 첨부파일이 있으면 업데이트
        if (file_name != null && !file_name.isEmpty()) {
            boardDao.update_attach(file_name, dto.getIdx());
        }

        return ResponseEntity.ok("게시글 수정 완료");
    }

    // 건의사항 삭제
    @DeleteMapping("delete/{idx}")
    public ResponseEntity<String> deleteSuggestion(@PathVariable("idx") int idx) {
        logger.info("Deleting suggestion ID: " + idx);
        boardService.delete(idx);
        return new ResponseEntity<>("Suggestion deleted successfully", HttpStatus.OK);
    }

	
    // 첨부 파일 목록 조회
    @GetMapping("list_attach/{idx}")
    public ResponseEntity<List<String>> getAttachments(@PathVariable("idx") int idx) {
        logger.info("📂 Fetching attachment list for suggestion ID: " + idx);
        List<String> attachments = boardService.list_attach(idx);
        
        if (attachments.isEmpty()) {
            logger.warn("⚠️ No attachments found for ID: " + idx);
        } else {
            logger.info("✅ Attachments fetched: " + attachments.size());
        }

        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }
    
 // ✅ 파일명을 DB에 저장하는 API 추가
    @PostMapping("/update_attach")
    public ResponseEntity<String> updateAttach(@RequestBody Map<String, Object> payload) {
        String fileName = (String) payload.get("file_name");
        int idx = (Integer) payload.get("idx");

        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일명이 없습니다.");
        }

        boardDao.update_attach(fileName, idx); // DB 저장
        return ResponseEntity.ok("파일이 DB에 저장되었습니다.");
    }

}