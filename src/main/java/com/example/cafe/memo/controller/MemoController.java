package com.example.cafe.memo.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.cafe.memo.model.MemoDAO;
import com.example.cafe.memo.model.MemoDTO;

@Controller
/* @RequestMapping("/memo") */
public class MemoController {

	@Autowired
	MemoDAO memoDao;

	@GetMapping("listMemo.do")
	public String listMemo(@RequestParam(name = "userid", required = false) String userid,
			@RequestParam(name = "cf_number", defaultValue = "0") int cf_number,
			@RequestParam(name = "cf_name", defaultValue = "") String cf_name, Model model) {
		System.out.println("카페 번호: " + cf_number);
		List<MemoDTO> list = memoDao.listMemo(userid, cf_number);
		model.addAttribute("list", list);
		model.addAttribute("count", list.size());
		model.addAttribute("userid", userid);
		model.addAttribute("cf_name", cf_name);
		model.addAttribute("cf_number", cf_number);
		// 전체 메모 vs 특정 카페 메모 구분
		if (cf_number == 0) {
			return "memo/listAllMemo";
		} else {
			return "memo/listCfMemo";
		}
	}

	
    @PostMapping("/insertMemo.do")
    public String insertMemo(@RequestParam("cf_number") long cf_number,
                             @RequestParam("userid") String userid,
                             @RequestParam("memo_title") String title,
                             @RequestParam("memo_content") String content,
                             @RequestParam("w_date") String wDate,
                             @RequestParam(name = "memo_file", required = false) MultipartFile memoFile,
                             Model model) throws IOException {

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

        model.addAttribute("message", "메모가 추가되었습니다!");
        return "redirect:/listMemo.do?userid=" + userid + "&cf_number=" + cf_number; 
    }
    
    /** 메모 상세 보기 (JSP로 데이터 전달) **/
    @GetMapping("/memoDetail.do")
    public String memoDetail(@RequestParam("memo_idx") long memo_idx, Model model) {
        MemoDTO memo = memoDao.detailMemo(memo_idx);
        
        if (memo == null) {
            System.out.println("❌ 메모를 찾을 수 없음! memo_idx: " + memo_idx);
            model.addAttribute("error", "메모를 찾을 수 없습니다.");
            return "memo/memoDetail";  // memoDetail.jsp에서 에러 메시지 출력
        }
        
        String encodedFile = memo.getMemo_file() != null ? Base64.getEncoder().encodeToString(memo.getMemo_file()) : "";

        model.addAttribute("memo", memo);
        model.addAttribute("encodedFile", encodedFile);

        System.out.println("📌 메모 상세 조회 - 인덱스: " + memo_idx);
        return "memo/memoDetail";
    }

    /** 메모 삭제 **/
    @GetMapping("/deleteMemo.do")
    public String deleteMemo(@RequestParam("memo_idx") long memo_idx, @RequestParam("userid") String userid,
                             @RequestParam("cf_number") long cf_number, Model model) {
        memoDao.deleteMemo(memo_idx);
        System.out.println("메모 삭제 완료: " + memo_idx);

        model.addAttribute("message", "메모가 삭제되었습니다!");
        return "redirect:/listMemo.do?userid=" + userid + "&cf_number=" + cf_number;
    }
}
