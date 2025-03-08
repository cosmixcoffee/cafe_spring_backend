package com.example.cafe;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// jsp관련 링크 컨트롤러 
@Controller
public class JPathController {
	
	//세션 테스트 
	@GetMapping("/info/sessionTest")
	public String test() {
	    return "/info/sessionTest"; 
	}
	
	
	// 메인 검색페이지 
	@GetMapping("/main")
	public String main() {
	    return "main"; 
	}
	
	//로그인 페이지
    @GetMapping("/log/login")
    public String login() {
        return "log/login"; 
    }
    
    //회원가입 페이지
    @GetMapping("/log/join")
    public String join() {
        return "log/join"; 
    }
    
    //카페 서브정보 입력
    @GetMapping("/log/join_cfinfo")
    public String join_cfinfo() {
        return "log/join_cfinfo";
    }
    
    //아이디 찾기
    @GetMapping("/log/idFind")
    public String idfind() {
        return "log/idFind";
    }
    
    //패스워드 찾기
    @GetMapping("/log/pwdFind")
    public String pwdFind() {
        return "log/pwdFind";
    }
    
    //마이 페이지
    @GetMapping("/mypage/mypage")
    public String mypage() {
        return "mypage/mypage";
    }
    
    //마이 페이지 기본 페이지
    @GetMapping("/mypage/mypageMain")
    public String mypageMain() {
        return "mypage/mypageMain";
    }
    
    
    //마이 페이지 프로필수정
    @GetMapping("/info/memberEdit")
    public String memberEdit() {
        return "info/memberEdit";
    }
    
    //마이 페이지 카페등록 페이지
    @GetMapping("/info/cafe_insert")
    public String cafe_insert() {
        return "info/cafe_insert";
    }
    
    //마이 페이지 카페정보수정 페이지
    @GetMapping("/info/cafeinfo_edit")
    public String cafeinfo_edit() {
        return "info/cafeinfo_edit";
    }


    
    //메모관련 전체리스트
    @GetMapping("/memo/listAllMemo")
    public String listAllMemo() {
        return "memo/listAllMemo";
    }
    
    //메모관련 카페리스트
    @GetMapping("/memo/listCfMemo")
    public String listCfMemo() {
        return "memo/listCfMemo";
    }
    
    //메모관련 디테일
    @GetMapping("/memo/memoDetail")
    public String memoDetail() {
        return "memo/memoDetail";
    }
    
    
    //메모관련 메모 쓰기
    @GetMapping("/memo/writeMemo")
    public String writeMemo() {
        return "memo/writeMemo";
    }

    
    //즐겨찾기관련 리스트
    @GetMapping("/favor/list_favorite")
    public String list_favorite() {
        return "favor/list_favorite";
    }
    

    //즐겨찾기관련 리스트
    @GetMapping("/search/cf_detail")
    public String cafe_detail() {
        return "search/cf_detail";
    }
    
	
	 //리뷰관련 리스트
  
	 @GetMapping("/review/review")
	 public String review() { 
		 return "review/review"; 
	 }

	 @GetMapping("review/writeReview")
	 public String writeReview(
	         @RequestParam(name="cf_number") int cf_number,
	         @RequestParam(name="cf_name") String cf_name,
	         @RequestParam(name="userid") String userid,
	         Model model) {

	     model.addAttribute("cf_number", cf_number);
	     model.addAttribute("cf_name", cf_name);
	     model.addAttribute("userid", userid);
	     
	     return "review/writeReview";  // View Resolver를 통해 JSP를 찾도록 설정
	 }
}
