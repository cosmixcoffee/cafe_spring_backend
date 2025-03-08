package com.example.cafe.log.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.example.cafe.search.model.CafeDTO;
import com.example.cafe.search.model.CafeImageDTO;

public interface CfMemberDAO {
	
	
	//로그인
	String login(CfAdminDTO adDto, CfManagerDTO mgDto, CfUserDTO uDto, String option);
	
	//회원가입
	void join(CfManagerDTO mgdto, CfUserDTO udto, String option);
	
	//아이디 중복체크
	boolean isIdExist(String userid);
	
	//비밀번호 중복체크
	String checkPasswd(String userid, int au_lv);
	//비밀번호 암호화
	String encryptPasswd(Map<String, Object> params);
	
	//아이디, 비번 찾기
	List<Map<String, Object>> findIds(String name, String tel, String email);
	Map<String, Object> findPasswds(String userid, String name, String tel, String email);
	// 비번 업데이트
	void updateUserPassword(@Param("userid") String userid, 
            @Param("passwd") String encryptedPassword, 
            @Param("auLv") int auLv);
	
	//회원정보수정
	int editMemberInfo(Map<String, Object> params);

	
	//카페중복등록확인
	CfManagerDTO getCafe(String mguserid);
	
	//사업자번호 중복확인 
	boolean isCodeExist(String cf_code);
	
	//카페 중복등록체크
	boolean isCafeRegistered(String userid);
	
	//카페 등록
	boolean insertCafeInfo(CafeDTO cfDto);
	
	//카페정보 수정
	void updateCafe(Map<String, Object> params);
	
	//카페이미지 등록관련
	List<CafeImageDTO> getImagesByCfNum(int cf_num);
	
	// 마이페이지 프로필관련
	CfAdminDTO getAdminInfo(String userid);
	CfManagerDTO getManagerInfo(String userid);
	CfUserDTO getUserInfo(String userid);
	
	// 카페넘버 저장
	int saveCafeNum(String userid);
	// 권한레벨 조회
	int getUserLevel(String userid);
	
}
