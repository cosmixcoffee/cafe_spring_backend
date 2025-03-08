package com.example.cafe.log.controller;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cafe.log.model.CfAdminDTO;
import com.example.cafe.log.model.CfManagerDTO;
import com.example.cafe.log.model.CfMemberDAO;
import com.example.cafe.log.model.CfUserDTO;
import com.example.cafe.log.service.EmailService;
import com.example.cafe.search.model.CafeDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	
	@Autowired
	CfMemberDAO cfmemberDao;  

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    EmailService emailService;
    
    @PostMapping("login.do")
    @ResponseBody
    public String login(
            @RequestParam(name="option", defaultValue = "") String option,
            @RequestParam(name="userid", defaultValue = "") String userid,
            @RequestParam(name="passwd", defaultValue = "") String rawPassword, // 입력된 비밀번호
            HttpSession session) {

        String name = null;
        Integer au_lv = null;

        if (!("admin".equals(option) || "manager".equals(option) || "user".equals(option))) {
            return "{\"status\":\"error\", \"message\":\"잘못된 요청입니다.\"}"; // JSON 응답
        }

        // 저장된 비밀번호 가져오기 (DB에서 해싱된 비밀번호 조회)
        String storedPassword = cfmemberDao.checkPasswd(userid, "admin".equals(option) ? 0 : "manager".equals(option) ? 1 : 2);

        // 🔍 비밀번호 검증 로그
        System.out.println("🔍 [DEBUG] 저장된 비밀번호: " + storedPassword);
        System.out.println("🔍 [DEBUG] 입력한 비밀번호: " + rawPassword);

        // 비밀번호 비교
        if (storedPassword == null || !passwordEncoder.matches(rawPassword, storedPassword)) {
            return "{\"status\":\"error\", \"message\":\"아이디 또는 비밀번호가 잘못되었습니다.\"}"; // 로그인 실패
        }

        if ("admin".equals(option)) {
            CfAdminDTO adminDto = new CfAdminDTO();
            adminDto.setUserid(userid);
            adminDto.setPasswd(storedPassword); // 해싱된 비밀번호 설정
            au_lv = 0;
            name = cfmemberDao.login(adminDto, null, null, "admin");
        } else if ("manager".equals(option)) {
            CfManagerDTO managerDto = new CfManagerDTO();
            managerDto.setUserid(userid);
            managerDto.setPasswd(storedPassword); // 해싱된 비밀번호 설정
            au_lv = 1;
            name = cfmemberDao.login(null, managerDto, null, "manager");
        } else if ("user".equals(option)) {
            CfUserDTO userDto = new CfUserDTO();
            userDto.setUserid(userid);
            userDto.setPasswd(storedPassword); // 해싱된 비밀번호 설정
            au_lv = 2;
            name = cfmemberDao.login(null, null, userDto, "user");
        }

        if (name == null) {
            return "{\"status\":\"error\", \"message\":\"아이디 또는 비밀번호가 잘못되었습니다.\"}"; // 로그인 실패
        }

        int cf_number = cfmemberDao.saveCafeNum(userid);
        session.setAttribute("cf_number", cf_number);
        session.setAttribute("user_role", option);
        session.setAttribute("userid", userid);
        session.setAttribute("user_name", name);
        session.setAttribute("user_au_lv", au_lv);
        session.setAttribute("result", name + "님 환영합니다.");

        return "{\"status\":\"success\", \"message\":\"로그인을 성공하셨습니다.\"}"; // ✅ JSON 응답 반환
    }

    
    @GetMapping("logout.do")
    public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/log/login?message=logout";
    }
    
    @PostMapping("join.do")
    @ResponseBody
    public String join(
            @RequestParam(name="selectedOption", defaultValue = "") String option,
            @RequestParam(name="userid", defaultValue = "") String userid,
            @RequestParam(name="passwd", defaultValue = "") String rawPassword, // 암호화 적용
            @RequestParam(name="name", defaultValue = "") String name,
            @RequestParam(name="email", defaultValue = "") String email,
            @RequestParam(name="tel", defaultValue = "") String tel,
            @RequestParam(name="cf_name", defaultValue = "") String cf_name,
            @RequestParam(name="cf_pcode", defaultValue = "") String cf_pcode,
            @RequestParam(name="cf_adr1", defaultValue = "") String cf_adr1,
            @RequestParam(name="cf_adr2", defaultValue = "") String cf_adr2,
            @RequestParam(name="cf_tel", defaultValue = "") String cf_tel,
            @RequestParam(name="au_lv", defaultValue = "") int au_lv) {

        // 아이디에 'admin' 포함 여부 확인
        if (userid.toLowerCase().contains("admin")) {
            return "{\"status\": \"error\", \"message\": \"아이디에 'admin'을 포함할 수 없습니다.\"}";
        }

        // 필수 값 검증
        if (userid.isEmpty() || rawPassword.isEmpty() || name.isEmpty() || tel.isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"필수 정보를 입력해주세요.\"}";
        }

        // 아이디 중복 체크
        if (cfmemberDao.isIdExist(userid)) {
            return "{\"status\": \"error\", \"message\": \"이미 존재하는 아이디입니다.\"}";
        }

        try {
            // 비밀번호 암호화 🔐
            String hashedPassword = passwordEncoder.encode(rawPassword);
            System.out.println("🔐 암호화된 비밀번호: " + hashedPassword);

            if ("manager".equals(option)) {
                CfManagerDTO managerDto = new CfManagerDTO();
                managerDto.setUserid(userid);
                managerDto.setPasswd(hashedPassword); // 암호화된 비밀번호 저장
                managerDto.setName(name);
                managerDto.setEmail(email);
                managerDto.setTel(tel);
                managerDto.setCf_name(cf_name);
                managerDto.setCf_pcode(cf_pcode);
                managerDto.setCf_adr1(cf_adr1);
                managerDto.setCf_adr2(cf_adr2 == null ? "" : cf_adr2); // null 방지
                managerDto.setCf_tel(cf_tel);
                managerDto.setAu_lv(au_lv);
                cfmemberDao.join(managerDto, null, "manager");

            } else if ("user".equals(option)) {
                CfUserDTO userDto = new CfUserDTO();
                userDto.setUserid(userid);
                userDto.setPasswd(hashedPassword); // 암호화된 비밀번호 저장
                userDto.setName(name);
                userDto.setEmail(email);
                userDto.setTel(tel);
                userDto.setAu_lv(au_lv);
                cfmemberDao.join(null, userDto, "user");
            } else {
                return "{\"status\": \"error\", \"message\": \"유효하지 않은 사용자 유형입니다.\"}";
            }

            return "{\"status\": \"success\", \"message\": \"회원가입이 완료되었습니다.\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"회원가입 처리 중 오류가 발생했습니다.\"}";
        }
    }

    
    @PostMapping("idCheck.do")
    @ResponseBody
    public String idCheck(@RequestParam(name="userid", required=true) String userid,
    		@RequestParam(name="selectedOption", required=true) String option) {
        //TODO: process POST request
    	
        // option 값 검증
        if (!("manager".equals(option) || "user".equals(option))) {
            return "{\"status\":\"error\", \"message\":\"유효하지 않은 사용자 유형입니다.\"}";
        }
        // 아이디 중복 체크
        boolean exists = cfmemberDao.isIdExist(userid);
        if (exists) {
            return "exists";  // 이미 존재하는 아이디
        } else {
            return "available";  // 사용 가능한 아이디
        }
    }
    
    @PostMapping("findID.do")
    public String findID(@RequestParam(name="name", required=true) String name,
    		@RequestParam(name="tel", required=true) String tel,
    		@RequestParam(name="email", required=true) String email,
    		Model model) {
       
    	List<Map<String, Object>> userids = cfmemberDao.findIds(name, tel, email);
    	
	    if (!userids.isEmpty()) {
	        model.addAttribute("userids", userids);
	    } else {
	        model.addAttribute("message", "입력하신 정보와 일치하는 아이디를 찾을 수 없습니다.");
	    }

	    return "log/findIdResult"; // JSP 파일명 반환
	}
    
    @PostMapping("findPWD.do")
    public String findPWD(@RequestParam(name="userid", required=true) String userid,
                          @RequestParam(name="name", required=true) String name,
                          @RequestParam(name="tel", required=true) String tel,
                          @RequestParam(name="email", required=true) String email,
                          Model model) {

        // 사용자 정보 조회
        Map<String, Object> userpwd = cfmemberDao.findPasswds(userid, name, tel, email);

        if (userpwd == null) {
            model.addAttribute("message", "입력하신 정보와 일치하는 계정을 찾을 수 없습니다.");
            return "log/findPwdResult"; // JSP 결과 페이지 반환
        }

        int auLv = 0;
        if (userpwd.get("AU_LV") != null) {
            try {
                auLv = Integer.parseInt(userpwd.get("AU_LV").toString());
            } catch (NumberFormatException e) {
                System.out.println("🔴 AU_LV 값 변환 실패: " + userpwd.get("AU_LV"));
                model.addAttribute("message", "유효하지 않은 사용자 등급입니다.");
                return "log/findPwdResult";
            }
        } else {
            model.addAttribute("message", "사용자 등급 정보를 찾을 수 없습니다.");
            return "log/findPwdResult";
        }

        // 임시 비밀번호 생성
        String tempPassword = generateTempPassword(8);
        String encryptedPassword = passwordEncoder.encode(tempPassword);

        System.out.println("생성된 임시 비밀번호: " + tempPassword);
        System.out.println("암호화된 비밀번호: " + encryptedPassword);

        // 암호화된 비밀번호 DB 업데이트
        cfmemberDao.updateUserPassword(userid, encryptedPassword, auLv);

        // 이메일로 임시 비밀번호 전송
        boolean emailSent = emailService.sendTemporaryPassword(email, tempPassword);

        if (!emailSent) {
            model.addAttribute("message", "이메일 전송에 실패했습니다. 관리자에게 문의하세요.");
            return "log/findPwdResult";
        }

        // 성공 시 JSP에서 사용할 데이터 추가
        model.addAttribute("userpwd", userpwd);
        model.addAttribute("email", email);
        model.addAttribute("au_lv", auLv);
        model.addAttribute("message", "임시 비밀번호가 이메일로 전송되었습니다.");

        return "log/findPwdResult"; // JSP 결과 페이지 반환
    }

    // ✅ 임시 비밀번호 생성 메서드
    private String generateTempPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    
    @GetMapping("memberEditInfo.do")
    @ResponseBody
    public Map<String, Object> memberEditInfo(@RequestParam(name="userid", required=true) String userid,
                                       @RequestParam(name="au_lv", defaultValue = "-1") int au_lv) {
        Map<String, Object> response = new HashMap<>();

        
        
        try {
            // 1. 유효성 검사
            if (userid == null || userid.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "로그인을 다시 해주십시오.");
                response.put("redirectUrl", "http://localhost/log/login"); 
                return response;
            }

            // 2. 사용자 정보 조회
            CfAdminDTO adminInfo = cfmemberDao.getAdminInfo(userid);
            CfManagerDTO managerInfo = cfmemberDao.getManagerInfo(userid);
            CfUserDTO userInfo = cfmemberDao.getUserInfo(userid);

            // 3. 사용자 정보가 없을 경우
            if (adminInfo == null && managerInfo == null && userInfo == null) {
                response.put("status", "error");
                response.put("message", "사용자 정보를 찾을 수 없습니다.");
                return response;
            }

            // 4. 정상 응답 생성
            response.put("status", "success");

            if (managerInfo != null) {
                Map<String, Object> manager = new HashMap<>();
                manager.put("userid", managerInfo.getUserid());
                manager.put("passwd", managerInfo.getPasswd());
                manager.put("cf_name", managerInfo.getCf_name());
                manager.put("name", managerInfo.getName());
                manager.put("email", managerInfo.getEmail());
                manager.put("tel", managerInfo.getTel());
                manager.put("au_lv", managerInfo.getAu_lv());
                response.put("manager", manager);
            }

            if (userInfo != null) {
                Map<String, Object> user = new HashMap<>();
                user.put("userid", userInfo.getUserid());
                user.put("passwd", userInfo.getPasswd());
                user.put("name", userInfo.getName());
                user.put("email", userInfo.getEmail());
                user.put("tel", userInfo.getTel());
                user.put("au_lv", userInfo.getAu_lv());
                response.put("user", user);
            }

            if (adminInfo != null) {
                Map<String, Object> admin = new HashMap<>();
                admin.put("userid", adminInfo.getUserid());
                admin.put("passwd", adminInfo.getPasswd());
                admin.put("name", adminInfo.getName());
                admin.put("email", adminInfo.getEmail());
                admin.put("tel", adminInfo.getTel());
                admin.put("au_lv", adminInfo.getAu_lv());
                response.put("admin", admin);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "정보를 조회하는 중 오류가 발생했습니다.");
        }

        return response;
    }
    
    
    @PostMapping("memberEdit.do")
    @ResponseBody
    public Map<String, Object> memberEdit(
            @RequestParam(name = "userid", required = true) String userid,
            @RequestParam(name = "passwd", required = false, defaultValue = "") String passwd,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "tel", required = false, defaultValue = "") String tel,
            @RequestParam(name = "user_au_lv", required = true) int user_au_lv, HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        
        // 세션에 저장된 로그인된 사용자 ID 확인
        String sessionUserid = (String) session.getAttribute("userid");

        if (sessionUserid == null || !sessionUserid.equals(userid)) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        // 입력값 검증
        if (passwd.trim().isEmpty() && email.trim().isEmpty() && tel.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "수정할 정보를 입력해주세요.");
            return response;
        }

        try {
            // 기존 비밀번호 가져오기
            String prevPwd = cfmemberDao.checkPasswd(userid, user_au_lv);

            // 새로운 비밀번호가 기존 비밀번호와 같으면 오류 발생
            if (!passwd.trim().isEmpty() && passwordEncoder.matches(passwd, prevPwd)) {
                response.put("status", "error");
                response.put("message", "새로운 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.");
                return response;
            }

            // 파라미터 맵 생성
            Map<String, Object> params = new HashMap<>();
            params.put("userid", userid);
            params.put("user_au_lv", user_au_lv);

            // 값이 있는 경우만 업데이트
            if (!passwd.trim().isEmpty()) {
                String hashedPassword = passwordEncoder.encode(passwd);
                params.put("passwd", hashedPassword);
            }
            if (!email.trim().isEmpty()) {
                params.put("email", email);
            }
            if (!tel.trim().isEmpty()) {
                params.put("tel", tel);
            }
            
            System.out.println("수정 요청된 회원 정보: " + params);

            // 회원정보 수정 실행
            int result = cfmemberDao.editMemberInfo(params); 

            // 업데이트된 행 개수 출력
            System.out.println("수정된 행 개수: " + result);
            
            if (result > 0) {
                response.put("status", "success");
                response.put("message", "회원정보가 성공적으로 수정되었습니다.");
            } else {
                response.put("status", "error");
                response.put("message", "회원정보 수정에 실패하였습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "회원정보 수정 중 오류가 발생했습니다. 관리자에게 문의하세요.");
        }

        return response;
    }


    /**
     * 🔹 비밀번호 확인 API 추가 (비밀번호 입력 후 확인 버튼 클릭 시 호출됨)
     */
    @PostMapping("inputPassword.do")
    @ResponseBody
    public Map<String, Object> checkPassword(
            @RequestParam(name = "userid", required = true) String userid,
            @RequestParam(name = "passwd", required = true) String passwd,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        
        // 세션에 저장된 로그인된 사용자 ID 확인
        String sessionUserid = (String) session.getAttribute("userid");

        if (sessionUserid == null || !sessionUserid.equals(userid)) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        // 현재 비밀번호 가져오기
        int user_au_lv = (int) session.getAttribute("user_au_lv");
        String storedPassword = cfmemberDao.checkPasswd(userid, user_au_lv);

        // 저장된 비밀번호와 입력된 비밀번호 비교
        if (storedPassword != null && passwordEncoder.matches(passwd, storedPassword)) {
            response.put("status", "success");
            response.put("message", "비밀번호 확인 성공");
        } else {
            response.put("status", "error");
            response.put("message", "비밀번호가 일치하지 않습니다.");
        }

        return response;
    }
    
    
    @GetMapping("profile.do")
    @ResponseBody
    public Map<String, Object> profile(@RequestParam(name="userid", required=true) String userid,
                                       @RequestParam(name="au_lv", defaultValue = "-1") int au_lv) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. 유효성 검사
            if (userid == null || userid.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "로그인을 다시 해주십시오.");
                response.put("redirectUrl", "http://localhost/log/login"); 
                return response;
            }

            // 2. 사용자 정보 조회
            CfAdminDTO adminInfo = cfmemberDao.getAdminInfo(userid);
            CfManagerDTO managerInfo = cfmemberDao.getManagerInfo(userid);
            CfUserDTO userInfo = cfmemberDao.getUserInfo(userid);

            // 3. 사용자 정보가 없을 경우
            if (adminInfo == null && managerInfo == null && userInfo == null) {
                response.put("status", "error");
                response.put("message", "사용자 정보를 찾을 수 없습니다.");
                return response;
            }

            // 4. 정상 응답 생성
            response.put("status", "success");

            if (managerInfo != null) {
                Map<String, Object> manager = new HashMap<>();
                manager.put("userid", managerInfo.getUserid());
                manager.put("cf_name", managerInfo.getCf_name());
                manager.put("name", managerInfo.getName());
                manager.put("email", managerInfo.getEmail());
                manager.put("tel", managerInfo.getTel());
                manager.put("au_lv", managerInfo.getAu_lv());
                response.put("manager", manager);
            }

            if (userInfo != null) {
                Map<String, Object> user = new HashMap<>();
                user.put("userid", userInfo.getUserid());
                user.put("name", userInfo.getName());
                user.put("email", userInfo.getEmail());
                user.put("tel", userInfo.getTel());
                user.put("au_lv", userInfo.getAu_lv());
                response.put("user", user);
            }

            if (adminInfo != null) {
                Map<String, Object> admin = new HashMap<>();
                admin.put("userid", adminInfo.getUserid());
                admin.put("name", adminInfo.getName());
                admin.put("email", adminInfo.getEmail());
                admin.put("tel", adminInfo.getTel());
                admin.put("au_lv", adminInfo.getAu_lv());
                response.put("admin", admin);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "정보를 조회하는 중 오류가 발생했습니다.");
        }

        return response;
    }

    // 마이페이지 카페등록 전, 카페정보 불러오기
    @GetMapping("checkcafe.do")
    @ResponseBody
    public String checkcafe(@RequestParam(name="userid", required=true) String userid) { 	
    	
        if (userid == null) {
            return "{\"status\":\"error\", \"message\":\"로그인을 다시 해주십시오.\"}";
        }
        try {
    		CfManagerDTO managerInfo   = cfmemberDao.getManagerInfo(userid);
    		
            if (managerInfo == null) {
                return "{\"status\": \"error\", \"message\": \"등록한 카페를 찾을 수 없습니다.\"}";
            }
            // JSON 문자열 구성
            StringBuilder json = new StringBuilder("{\"status\": \"success\", ");
            if (managerInfo != null) {
                json.append(String.format(
                    "\"manager\": {\"userid\": \"%s\", \"cf_name\": \"%s\", \"cf_pcode\": \"%s\", \"cf_adr1\": \"%s\", \"cf_adr2\": \"%s\", \"cf_tel\": \"%s\"}, ",
                    managerInfo.getUserid(),
                    managerInfo.getCf_name(),
                    managerInfo.getCf_pcode(),
                    managerInfo.getCf_adr1(),
                    managerInfo.getCf_adr2(),
                    managerInfo.getCf_tel()
                ));
            }
            // 마지막 쉼표와 공백 제거
            json.setLength(json.length() - 2);
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"카페 정보를 조회하는 중 오류가 발생했습니다.\"}";
        }
    }
 // 마이페이지 카페등록 전, 사업자 번호 체크 
    @PostMapping("codeCheck.do")
    @ResponseBody
    public String codeCheck(@RequestParam(name="cf_code", required=true) String cf_code) {

        // 아이디 중복 체크
        boolean exists = cfmemberDao.isCodeExist(cf_code);
        if (exists) {
            return "exists";  // 이미 존재하는 코드
        } else {
            return "available";  // 사용 가능한 코드
        }
    }
    

    @PostMapping("cafein.do")
    @ResponseBody
    public Map<String, Object> cafein(@RequestParam(name="userid") String userid,
    		@RequestParam(name="cf_name") String cf_name,
    		@RequestParam(name="cf_code") String cf_code,
    		@RequestParam(name="cf_pcode") String cf_pcode,
    		@RequestParam(name="cf_adr1") String cf_adr1,
    		@RequestParam(name="cf_adr2", defaultValue = "") String cf_adr2,
    		@RequestParam(name="cf_tel") String cf_tel,
    		@RequestParam(name="cf_content", defaultValue = "") String cf_content, HttpSession session) {
    	
    	Map<String, Object> response = new HashMap<>();
	    
	    //코드 빈칸 체크
	    if (cf_code == null || cf_code.trim().isEmpty()) {
	        response.put("status", "error");
	        response.put("message", "사업자번호를 입력해주세요.");
	        return response;
	    }
	    
	    // 서버 측 사업코드 체크
        if (cfmemberDao.isCodeExist(cf_code)) {
        	response.put("status", "error");
	        response.put("message", "이미 등록된 사업자코드입니다.");
	        return response;
        }
        
        // 카페 등록 여부 확인
        if (cfmemberDao.isCafeRegistered(userid)) {
        	response.put("status", "error");
	        response.put("message", "이미 카페를 등록했습니다.");
	        response.put("redirect", "/mypage/mypage");
	        return response;
        }
        
        // 카페정보 등록 
		CafeDTO cafeDto = new CafeDTO();
        cafeDto.setUserid(userid);
        cafeDto.setCf_code(cf_code);
        cafeDto.setCf_name(cf_name);
        cafeDto.setCf_pcode(cf_pcode);
        cafeDto.setCf_adr1(cf_adr1);
        cafeDto.setCf_adr2(cf_adr2);
        cafeDto.setCf_tel(cf_tel);
        cafeDto.setCf_content(cf_content);
        
        try {
        	boolean isInserted = cfmemberDao.insertCafeInfo(cafeDto);
        	if (isInserted) {
                int cf_number = cfmemberDao.saveCafeNum(userid);
                session.setAttribute("cf_number", cf_number);
                
                System.out.println("cf_number: " + cf_number);
                
                response.put("status", "success");
                response.put("message", "카페 정보가 성공적으로 등록되었습니다.");
                response.put("cf_number", cf_number);
                response.put("redirect", "/mypage/mypage");
			} else {
                response.put("status", "error");
                response.put("message", "카페 등록에 실패하셨습니다.");
                response.put("redirect", "/mypage/mypage");
			}
		} catch (Exception e) {
            response.put("status", "error");
            response.put("message", "서버 오류 발생:" + e.getMessage());
		}
        return response;
    }
}
