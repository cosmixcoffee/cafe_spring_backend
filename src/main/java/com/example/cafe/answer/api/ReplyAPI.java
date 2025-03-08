package com.example.cafe.answer.api;

import com.example.cafe.answer.model.ReplyDTO;
import com.example.cafe.answer.service.BoardService;
import com.example.cafe.answer.service.PageUtil;

import jakarta.servlet.http.HttpSession;

import com.example.cafe.answer.model.BoardDTO;
import com.example.cafe.answer.model.ReplyDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/reply/*")
public class ReplyAPI {
	
	private static final Logger logger = LoggerFactory.getLogger(ReplyAPI.class);

    @Autowired
    ReplyDAO replyDao;
    
    @Autowired
    BoardService boardService;

    @PostMapping("insert")
    public ResponseEntity<String> insert(
            @RequestBody ReplyDTO dto, 
            @CookieValue(value = "userid", required = false) String userid,
            @CookieValue(value = "user_au_lv", required = false) Integer user_au_lv) {

        if (userid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        // ✅ 디버깅 로그 추가
        logger.info("📌 댓글 작성 요청: board_idx = " + dto.getBoard_idx());
        logger.info("📌 댓글 내용: reply_text = " + dto.getReply_text());
        logger.info("📌 댓글 작성자: replyer = " + userid);
        logger.info("📌 사용자 권한: user_au_lv = " + user_au_lv);

        if (dto.getReply_text() == null || dto.getReply_text().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("댓글 내용이 비어 있습니다.");
        }

        // ✅ 게시글 작성자 확인
        BoardDTO board = boardService.userDetail(dto.getBoard_idx());
        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("게시글을 찾을 수 없습니다.");
        }

        // ✅ 본인 또는 관리자만 댓글 작성 가능
        boolean isOwner = userid.equals(board.getWriter());
        boolean isAdmin = user_au_lv != null && user_au_lv == 0;

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("댓글 작성 권한이 없습니다.");
        }

        try {
            dto.setReplyer(userid); // ✅ 댓글 작성자 설정
            replyDao.insert(dto);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 등록 중 오류 발생");
        }
    }





    @DeleteMapping("delete/{idx}") 
    public ResponseEntity<Map<String, String>> delete(@PathVariable(name = "idx") int idx) {
        Map<String, String> response = new HashMap<>();
        try {
            replyDao.delete(idx);
            response.put("status", "success"); // ✅ JSON 형식으로 응답
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("detail/{idx}")
    public ResponseEntity<ReplyDTO> detail(@PathVariable(name = "idx") int idx) {
        ReplyDTO dto = replyDao.detail(idx);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<?> list(
            @RequestParam(name = "board_idx", required = false) Integer board_idx,  // 🛑 NULL 방지
            @CookieValue(value = "userid", required = false) String userid,  // 🛑 NULL 방지
            @CookieValue(value = "user_au_lv", required = false) Integer userAuLv,
            @RequestParam(name = "curPage", defaultValue = "1") int curPage) {

        if (board_idx == null) {
            return new ResponseEntity<>("게시글 ID가 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        if (userAuLv == null) {
            userAuLv = 2;  // 🛑 기본값 설정 (일반 사용자)
        }

        logger.info("📌 댓글 조회 요청 - board_idx: {}, userid: {}, userAuLv: {}", board_idx, userid, userAuLv);

        int count = replyDao.count(board_idx);
        PageUtil page_info = new PageUtil(count, curPage);
        int start = page_info.getPageBegin();
        int end = page_info.getPageEnd();

        List<ReplyDTO> list;

        if (userAuLv == 0) {
            list = replyDao.adminList(board_idx, start, end);
        } else if (userAuLv == 1) {
            if (userid == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            list = replyDao.manager_jsonList(board_idx, start, end, userid);
        } else {
            if (userid == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            list = replyDao.user_jsonList(board_idx, start, end, userid);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }






    @PostMapping("/update/{idx}")
    public ResponseEntity<String> update(@PathVariable(name = "idx") int idx,
                                         @RequestBody ReplyDTO dto) {
        try {
            dto.setIdx(idx);
            replyDao.update(dto);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
