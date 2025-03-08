package com.example.cafe.memo.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.cafe.memo.model.MemoDAO;
import com.example.cafe.memo.model.MemoDTO;

import ch.qos.logback.core.net.SyslogOutputStream;
import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

@RestController
@RequestMapping("/api/memo/")
/* @RequestMapping("/memo") */
public class MemoControllerAPI {

	@Autowired
	MemoDAO memoDao;

	@GetMapping("listMemo.do")
	//단순 MAP을 쓰는것 보다는 ResponseEntity를 사용하는 것이 성공, 에러등의 응답을 다양하게 활용할 수 있어 세밀하게 제어할 수 있다.
	public ResponseEntity<Map<String, Object>> listMemo(
	        @RequestParam(name = "userid", required = false) String userid,
	        @RequestParam(name = "cf_number", defaultValue = "0") int cf_number,
	        @RequestParam(name = "cf_name", defaultValue = "") String cf_name) {
	    
	    List<MemoDTO> list = memoDao.listMemo(userid, cf_number);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("count", list.size());
	    response.put("userid", userid);
	    response.put("cf_name", cf_name);
	    response.put("cf_number", cf_number);
	    
	    return ResponseEntity.ok(response);
	}

	
    @PostMapping("insertMemo.do")
    public ResponseEntity<Map<String, Object>> insertMemo(@RequestParam("cf_number") long cf_number,
                             @RequestParam("userid") String userid,
                             @RequestParam("memo_title") String title,
                             @RequestParam("memo_content") String content,
                             @RequestParam("w_date") String wDate,
                             @RequestParam(name = "memo_file", required = false) MultipartFile memoFile,
                             Model model) throws IOException {
    	
    	
    	//파일이 null인지 확인후 바이트코드로 저장
        byte[] fileBytes = memoFile != null && !memoFile.isEmpty() ? memoFile.getBytes() : null;

        MemoDTO dto = new MemoDTO();
        dto.setCf_number(cf_number);
        dto.setUserid(userid);
        dto.setMemo_title(title);
        dto.setMemo_content(content);
        dto.setMemo_file(fileBytes);
        dto.setW_date(LocalDate.parse(wDate, DateTimeFormatter.ISO_LOCAL_DATE));

        memoDao.insertMemo(dto);

        System.out.println("메모 추가 완료:" + title);

        //JSON 으로 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("message","메모가 추가되었습니다!");
        response.put("redirectUrl", "/listMemo.do?userid="+userid+"&cf_number="+cf_number);
        return ResponseEntity.ok(response);
    }
    
    /** 메모 상세 보기 (JSP로 데이터 전달) **/
    @GetMapping("memoDetail.do")
    public ResponseEntity<Map<String, Object>> memoDetail(@RequestParam("memo_idx") long memo_idx) {
        MemoDTO memo = memoDao.detailMemo(memo_idx);
        
        if (memo == null) {
            System.out.println("❌ 메모를 찾을 수 없음! memo_idx: " + memo_idx);
            return ResponseEntity.badRequest().body(Map.of("error", "메모를 찾을 수 없습니다!"));
        }
        
        String encodedFile = memo.getMemo_file() != null ? Base64.getEncoder().encodeToString(memo.getMemo_file()) : "";

        Map<String, Object> response = new HashMap<>();
        System.out.println("📌 메모 상세 조회 - 인덱스: " + memo_idx);
        response.put("memo", memo);
        response.put("encodedFile", encodedFile);
        
        return ResponseEntity.ok(response);
    }

    /** 메모 삭제 **/
    @DeleteMapping("deleteMemo.do")
    public ResponseEntity<Map<String, Object>> deleteMemo(@RequestParam("memo_idx") long memo_idx, @RequestParam("userid") String userid,
                             @RequestParam("cf_number") long cf_number, Model model) {
        memoDao.deleteMemo(memo_idx);
        System.out.println("메모 삭제 완료: " + memo_idx);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "메모가 삭제되었습니다!");
        
        return ResponseEntity.ok(response);
    }
}
