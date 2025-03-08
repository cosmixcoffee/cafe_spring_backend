package com.example.cafe.log.api;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.cafe.search.model.CafeDTO;
import com.example.cafe.log.model.CfAdminDTO;
import com.example.cafe.log.model.CfManagerDTO;
import com.example.cafe.log.model.CfMemberDAO;
import com.example.cafe.log.model.CfUserDTO;
import com.example.cafe.log.service.EmailService;

import jakarta.servlet.http.HttpSession;
//json으로 응답을 해줘야함 
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/member/*")
public class MemberApi {
	
	@Autowired
	CfMemberDAO cfmemberDao;  
	
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    EmailService emailService;
	
	
	@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("session")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 세션에서 userid 가져오기
        String userid = (String) session.getAttribute("userid");

        if (userid != null) {
            response.put("status", "success");
            response.put("userid", userid);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }
    }


    @PostMapping("login") 
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> reqLogData, HttpSession session) {
		/* System.out.println("백엔드에서 받은 데이터: " + reqLogData); */
    	
    	if (reqLogData == null || !reqLogData.containsKey("userid") || !reqLogData.containsKey("passwd") || !reqLogData.containsKey("option")) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "필수 데이터가 없습니다."));
        }
    	
    	String option = reqLogData.get("option");
		String userid = reqLogData.get("userid");
		String inputPassword = reqLogData.get("passwd");	
		 
		Map<String, Object> response = new HashMap<>();
		String name = null;
		Integer au_lv = null;
		
	    if (!("admin".equals(option) || "manager".equals(option) || "user".equals(option))) {
	        response.put("status", "error");
	        response.put("message", "잘못된 요청입니다.");
	        return ResponseEntity.badRequest().body(response);
	    }
	    
	    // 1 DB에서 저장된 암호화된 비밀번호 가져오기
	    String storedPassword = cfmemberDao.checkPasswd(userid, "admin".equals(option) ? 0 : "manager".equals(option) ? 1 : 2);

	    if (storedPassword == null) {
	        response.put("status", "error");
	        response.put("message", "아이디 또는 비밀번호가 잘못되었습니다.");
	        return ResponseEntity.ok(response);
	    }
	    
	    // 2️ 입력한 비밀번호와 DB에서 가져온 해시된 비밀번호를 비교
	    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    boolean isMatch = encoder.matches(inputPassword, storedPassword);
	    System.out.println("비밀번호 일치 여부: " + isMatch);
	    
	    // 비밀번호가 BCrypt 형식인지 확인
	    if (!storedPassword.startsWith("$2a$")) {
	        response.put("status", "error");
	        response.put("message", "비밀번호가 해싱되지 않았습니다. 관리자에게 문의하세요.");
	        return ResponseEntity.ok(response);
	    }

	    // ✅ 3️⃣ 입력한 비밀번호와 해시된 비밀번호 비교
	    if (!encoder.matches(inputPassword, storedPassword)) {
	        response.put("status", "error");
	        response.put("message", "아이디 또는 비밀번호가 잘못되었습니다.");
	        return ResponseEntity.ok(response);
	    }
	    
        if ("admin".equals(option)) {
			CfAdminDTO adminDto = new CfAdminDTO();
			au_lv = 0;
			adminDto.setUserid(userid);
			adminDto.setPasswd(storedPassword);
			name = cfmemberDao.login(adminDto, null, null, "admin");
			System.out.println("01: " + name);
		} else if("manager".equals(option)) {
			CfManagerDTO managerDto = new CfManagerDTO();
			managerDto.setUserid(userid);
			managerDto.setPasswd(storedPassword);
			au_lv = 1;
			name = cfmemberDao.login(null, managerDto, null, "manager");
		} else if("user".equals(option)) {
			CfUserDTO userDto = new CfUserDTO();
			userDto.setUserid(userid);
			userDto.setPasswd(storedPassword);
			au_lv = 2;
			name = cfmemberDao.login(null, null, userDto, "user");
		}
        
        if (name == null) {
            response.put("status", "error");
            response.put("message", "아이디 또는 비밀번호가 잘못되었습니다.");
            return ResponseEntity.ok(response);
        }
        System.out.println("세션 저장 전: " + session.getId());
        
        int cf_number = cfmemberDao.saveCafeNum(userid);
        session.setAttribute("cf_number", cf_number);
		session.setAttribute("user_role", option);
		session.setAttribute("userid", userid);
		session.setAttribute("user_name", name);
		session.setAttribute("user_au_lv", au_lv);
		
		System.out.println("세션 저장 완료: userid=" + session.getAttribute("userid"));
		
		response.put("status", "success");
		response.put("message", "로그인성공");
		
		session.setAttribute("result", name + "님 환영합니다.");
	        
			
		session.setAttribute("cf_number", cf_number);
		System.out.println("cf_number: " + cf_number);
		 
		session.setAttribute("user_role", option);
		session.setAttribute("userid", userid);
		session.setAttribute("user_name", name);
		session.setAttribute("user_au_lv", au_lv);
		
		response.put("status", "success");
		response.put("message", "로그인 성공");
		response.put("userid", userid);
		response.put("cf_number",cf_number);
		response.put("user_name", name);
		response.put("user_au_lv", au_lv);
		
		return ResponseEntity.ok(response);
		
	}
    
    @PostMapping("logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
		session.invalidate();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "로그인 완료");
		return ResponseEntity.ok(response);
    }
    
    @PostMapping("join")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> join(@RequestBody Map<String, Object> memberData) {
    	System.out.println("회원가입 요청 수신: " + memberData); 
    	Map<String, Object> response = new HashMap<>();
    	String option = (String) memberData.get("option");
    	String userid =(String) memberData.get("userid");
    	String rawPassword = (String) memberData.get("passwd"); // 입력된 비밀번호
    	
    	
    	// 아이디에 'admin' 포함 여부 확인
        if (userid.toLowerCase().contains("admin")) {
            response.put("status", "error");
            response.put("message", "아이디에 'admin'을 포함할 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        if (cfmemberDao.isIdExist(userid)) {
            response.put("status", "error");
            response.put("message", "이미 존재하는 아이디입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!("manager".equals(option) || "user".equals(option))) {
            response.put("status", "error");
            response.put("message", "유효하지 않은 사용자 유형입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            boolean isInserted = false;
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encryptedPassword = encoder.encode(rawPassword); // 비밀번호 암호화
            System.out.println("🔐 암호화된 비밀번호: " + encryptedPassword);

        	
			if ("manager".equals(option)) {
				CfManagerDTO managerDto = new CfManagerDTO();
				managerDto.setUserid(userid);
				managerDto.setPasswd(encryptedPassword);
                managerDto.setName((String) memberData.get("name"));
                managerDto.setEmail((String) memberData.get("email"));
                managerDto.setTel((String) memberData.get("tel"));
                managerDto.setCf_name((String) memberData.get("cf_name"));
                managerDto.setCf_pcode((String) memberData.get("cf_pcode"));
                managerDto.setCf_adr1((String) memberData.get("cf_adr1"));
                managerDto.setCf_adr2((String) memberData.getOrDefault("cf_adr2", "")); // null 방지
                managerDto.setCf_tel((String) memberData.get("cf_tel"));
                managerDto.setAu_lv((Integer) memberData.get("au_lv"));
                System.out.println("매니저 회원가입 데이터: " + managerDto); 
                
                System.out.println("🚀 매니저 회원가입 시작: " + managerDto); // ✅ DTO 확인 로그
                cfmemberDao.join(managerDto, null, "manager");
                System.out.println("🚀 cfmemberDao.join() 호출 완료! (매니저)");
                isInserted = true;
                
                
			} else if ("user".equals(option)) {
				CfUserDTO userDto = new CfUserDTO();
				userDto.setUserid(userid);
				userDto.setPasswd(encryptedPassword); 
                userDto.setName((String) memberData.get("name"));
                userDto.setEmail((String) memberData.get("email"));
                userDto.setTel((String) memberData.get("tel"));
                userDto.setAu_lv((Integer) memberData.get("au_lv"));
                System.out.println("사용자 회원가입 데이터: " + userDto); // ✅ DTO 확인 로그

                System.out.println("사용자 회원가입 시작: " + userDto); // ✅ DTO 확인 로그
                cfmemberDao.join(null, userDto, "user");
                System.out.println("cfmemberDao.join() 호출 완료! (사용자)");
                isInserted = true;
			} 
			if (isInserted) {
	            response.put("status", "success");
	            response.put("message", "회원가입이 완료되었습니다.");
	        } else {
	            response.put("status", "error");
	            response.put("message", "회원가입 실패. 다시 시도해주세요.");
	        }

	        return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "error");
            response.put("message", "회원가입 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
		}
    }
    
    @PostMapping("idCheck")
    public ResponseEntity<Map<String, Object>> idCheck(@RequestBody Map<String, String> params) {
    	 String userid = params.get("userid");
    	Map<String, Object> response = new HashMap<>();

        boolean exists = cfmemberDao.isIdExist(userid);
        
        System.out.println("아이디 중복 체크 요청: " + userid + " -> " + (exists ? "exists" : "available"));
        response.put("status", exists ? "exists" : "available");
      
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("findID")
    public ResponseEntity<Map<String, Object>> findID(@RequestBody Map<String, String> idData) {
        String name = idData.get("name");
        String tel = idData.get("tel");
        String email = idData.get("email");

        Map<String, Object> response = new HashMap<>();

        if (name == null || tel == null || email == null) {
            response.put("status", "error");
            response.put("message", "필수 입력값이 누락되었습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Map<String, Object>> userids = cfmemberDao.findIds(name, tel, email);

        if (!userids.isEmpty()) {
            response.put("status", "success");
            response.put("userids", userids);
        } else {
            response.put("status", "error");
            response.put("message", "입력하신 정보와 일치하는 아이디를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(response);
    }


    
	/*
	 * @PostMapping("findPWD") public ResponseEntity<Map<String, Object>>
	 * findPWD(@RequestBody Map<String, String> pwdData) { Map<String, Object>
	 * response = new HashMap<>(); // findPasswds() 호출 시 단일 문자열 반환 Map<String,
	 * Object> userpwd = cfmemberDao.findPasswds( pwdData.get("userid"),
	 * pwdData.get("name"), pwdData.get("tel"), pwdData.get("email") );
	 * 
	 * if (userpwd != null) { response.put("status", "success");
	 * response.put("userpwd", userpwd); } else { response.put("status", "error");
	 * response.put("message", "입력하신 정보와 일치하는 패스워드를 찾을 수 없습니다."); } return
	 * ResponseEntity.ok(response); }
	 */
    
    @PostMapping("findPWD")
    public ResponseEntity<Map<String, Object>> findPWD(@RequestBody Map<String, String> requestData){
        String userid = requestData.get("userid");
        String name = requestData.get("name");
        String tel = requestData.get("tel");
        String email = requestData.get("email");
    	
    	Map<String, Object> response = new HashMap<>();
    	
    	 // findPasswds() 호출 시 단일 문자열 반환
        Map<String, Object> userpwd = cfmemberDao.findPasswds(userid, name, tel, email);
        
        if (userpwd == null) {
            response.put("status", "error");
            response.put("message", "입력하신 정보와 일치하는 계정을 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        
        int auLv = 0;
        if (userpwd.get("AU_LV") != null) {
            try {
                auLv = Integer.parseInt(userpwd.get("AU_LV").toString());
            } catch (NumberFormatException e) {
                System.out.println("🔴 AU_LV 값 변환 실패: " + userpwd.get("AU_LV"));
                response.put("status", "error");
                response.put("message", "유효하지 않은 사용자 등급입니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            System.out.println("🔴 AU_LV 값이 NULL 입니다.");
            response.put("status", "error");
            response.put("message", "사용자 등급 정보를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        
		/* int auLv = Integer.parseInt(userpwd.get("AU_LV").toString()); */
    	
        // 새로운 비밀번호 임시생성
        String tempPassword = generateTempPassword(8);
        String encryptedPassword = passwordEncoder.encode(tempPassword);
        
        System.out.println("생성된 임시 비밀번호: " + tempPassword);
        System.out.println("암호화된 비밀번호: " + encryptedPassword);
        
        System.out.println("🔹 DB 업데이트 실행");
        System.out.println("userid: " + userid);
        System.out.println("encryptedPassword: " + encryptedPassword);
        System.out.println("auLv: " + auLv);
        
        // 암호화된 임시 비밀번호 DB에 업데이트 
        cfmemberDao.updateUserPassword(userid, encryptedPassword, auLv);
        
        
        // 이메일로 임시 비밀번호 전송 
        boolean emailSent = emailService.sendTemporaryPassword(email, tempPassword);

        if (!emailSent) {
            response.put("status", "error");
            response.put("message", "이메일 전송에 실패했습니다. 관리자에게 문의하세요.");
            return ResponseEntity.internalServerError().body(response);
        }

        response.put("status", "success");
        response.put("message", "임시 비밀번호가 이메일로 전송되었습니다.");
        return ResponseEntity.ok(response);
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
    
    
    @GetMapping("memberEditInfo")
    public Map<String, Object> getMemberEditInfo(@RequestParam(name="userid") String userid, @RequestParam(name="au_lv") int au_lv) {
        Map<String, Object> response = new HashMap<>();
        
        if (userid == null || userid.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "로그인을 다시 해주십시오.");
            return response;
        }
        
        CfAdminDTO adminInfo = cfmemberDao.getAdminInfo(userid);
        CfManagerDTO managerInfo = cfmemberDao.getManagerInfo(userid);
        CfUserDTO userInfo = cfmemberDao.getUserInfo(userid);
        
        if (adminInfo == null && managerInfo == null && userInfo == null) {
            response.put("status", "error");
            response.put("message", "사용자 정보를 찾을 수 없습니다.");
            return response;
        }
        
        response.put("status", "success");
        response.put("admin", adminInfo);
        response.put("manager", managerInfo);
        response.put("user", userInfo);
        
        return response;
    }
    
    @PutMapping("memberEdit")
    @ResponseBody
    public Map<String, Object> memberEdit(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        try {
            // 필수 값 확인
            String userid = (String) requestData.get("userid");
            Integer user_au_lv = (Integer) requestData.get("user_au_lv"); 
            String passwd = (String) requestData.getOrDefault("passwd", "");
            String email = (String) requestData.getOrDefault("email", "");
            String tel = (String) requestData.getOrDefault("tel", "");

            // 유효성 검사
            if (userid == null || userid.trim().isEmpty() || user_au_lv == null) {
                response.put("status", "error");
                response.put("message", "회원 정보가 유효하지 않습니다.");
                return response;
            }

            if (passwd.trim().isEmpty() && email.trim().isEmpty() && tel.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "수정할 정보를 입력해주세요.");
                return response;
            }
            
            String prevEncodedPwd = cfmemberDao.checkPasswd(userid, user_au_lv);
            if (!passwd.trim().isEmpty() && passwordEncoder.matches(passwd, prevEncodedPwd)) {
                response.put("status", "error");
                response.put("message", "새로운 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.");
                return response;
            }


            // 업데이트할 데이터 맵 생성
            Map<String, Object> params = new HashMap<>();
            params.put("userid", userid);
            params.put("user_au_lv", user_au_lv);

            // 값이 있는 경우만 추가
            if (!passwd.trim().isEmpty()) {
                String encodedPasswd = passwordEncoder.encode(passwd);
                params.put("passwd", encodedPasswd);
            }
            if (!email.trim().isEmpty()) params.put("email", email);
            if (!tel.trim().isEmpty()) params.put("tel", tel);

            System.out.println("🔍 수정 요청된 회원 정보: " + params);

            // 회원정보 수정 실행
            int result = cfmemberDao.editMemberInfo(params);

            // 업데이트된 행 개수 출력
            System.out.println("✅ 수정된 행 개수: " + result);

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

    
    // 정보수정용 비밀번호체크
    
    
    @PostMapping("inputPassword")
    public ResponseEntity<Map<String, Object>> inputPassword(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // ✅ BCrypt 인코더 추가
        
        String userid = requestData.get("userid");
        String inputPassword = requestData.get("password");

        if (userid == null || userid.trim().isEmpty() || inputPassword == null || inputPassword.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "아이디 또는 비밀번호를 입력하세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            System.out.println("🔍 [DEBUG] 사용자 ID: " + userid);

            int au_lv = cfmemberDao.getUserLevel(userid);
            System.out.println("🔍 [DEBUG] 조회된 au_lv: " + au_lv);

            String storedPassword = cfmemberDao.checkPasswd(userid, au_lv);
            System.out.println("🔍 [DEBUG] DB 저장된 비밀번호: " + storedPassword);
            System.out.println("🔍 [DEBUG] 입력한 비밀번호: " + inputPassword);

            // ✅ BCrypt로 비밀번호 비교
            if (storedPassword != null && encoder.matches(inputPassword, storedPassword)) {
                response.put("status", "success");
                response.put("message", "비밀번호가 일치합니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "비밀번호가 올바르지 않습니다.");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "서버 오류 발생.");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    
    @GetMapping("profile")
    public Map<String, Object> getProfile(@RequestParam(name = "userid") String userid, @RequestParam(name = "au_lv") int au_lv) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("프로필 요청 - userid: " + userid + ", au_lv: " + au_lv);

        if (userid == null || userid.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "로그인을 다시 해주십시오.");
            return response;
        }

        try {
            Object userInfo = null; // 다양한 DTO 타입을 받을 수 있도록 Object 선언

            if (au_lv == 0) { // 관리자
                userInfo = cfmemberDao.getAdminInfo(userid);
            } else if (au_lv == 1) { // 매니저
                userInfo = cfmemberDao.getManagerInfo(userid);
            } else { // 일반 사용자
                userInfo = cfmemberDao.getUserInfo(userid);
            }

            if (userInfo == null) {
                System.out.println("❌ 사용자 정보 없음: " + userid);
                response.put("status", "error");
                response.put("message", "프로필 정보를 찾을 수 없습니다.");
                return response;
            }

            System.out.println("사용자 정보 확인됨: " + userInfo);

            response.put("status", "success");
            response.put("user", userInfo);
            return response;

        } catch (Exception e) {
            System.out.println("❌ 프로필 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();  // 백엔드 콘솔에서 전체 에러 로그 확인
            response.put("status", "error");
            response.put("message", "서버 내부 오류 발생.");
            return response;
        }
    }

    
    @PostMapping("codeCheck")
    public Map<String, String> checkBusinessCode(@RequestParam String cf_code) {
        Map<String, String> response = new HashMap<>();
        boolean exists = cfmemberDao.isCodeExist(cf_code);
        response.put("status", exists ? "exists" : "available");
        return response;
    }
    
    @PostMapping("cafeIn")
    public Map<String, Object> registerCafe(@RequestBody CafeDTO cafeDto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        if (cfmemberDao.isCodeExist(cafeDto.getCf_code())) {
            response.put("status", "error");
            response.put("message", "이미 등록된 사업자코드입니다.");
            return response;
        }
        
        if (cfmemberDao.isCafeRegistered(cafeDto.getUserid())) {
            response.put("status", "error");
            response.put("message", "이미 카페를 등록했습니다.");
            return response;
        }
        
        boolean isInserted = cfmemberDao.insertCafeInfo(cafeDto);
        if (isInserted) {
            int cf_number = cfmemberDao.saveCafeNum(cafeDto.getUserid());
            session.setAttribute("cf_number", cf_number);
            response.put("status", "success");
            response.put("message", "카페 정보가 성공적으로 등록되었습니다.");
        } else {
            response.put("status", "error");
            response.put("message", "카페 등록에 실패했습니다.");
        }
        
        return response;
    }
    
    @GetMapping("checkCafe")
    public ResponseEntity<Map<String, Object>> checkCafe(@RequestParam("userid") String userid) {
        Map<String, Object> response = new HashMap<>();

        if (userid == null || userid.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "로그인을 다시 해주십시오.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            CfManagerDTO managerInfo = cfmemberDao.getManagerInfo(userid);

            if (managerInfo == null) {
                response.put("status", "error");
                response.put("message", "등록한 카페를 찾을 수 없습니다.");
                return ResponseEntity.ok(response);
            }

            Map<String, Object> cafeInfo = new HashMap<>();
            cafeInfo.put("userid", managerInfo.getUserid());
            cafeInfo.put("cf_name", managerInfo.getCf_name());
            cafeInfo.put("cf_pcode", managerInfo.getCf_pcode());
            cafeInfo.put("cf_adr1", managerInfo.getCf_adr1());
            cafeInfo.put("cf_adr2", managerInfo.getCf_adr2());
            cafeInfo.put("cf_tel", managerInfo.getCf_tel());

            response.put("status", "success");
            response.put("manager", cafeInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "카페 정보를 조회하는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //  memberEditInfo.do,  memberEdit.do, profile.do, checkcafe.do, codeCheck.do, cafein.do 추후 추가 예정
}
