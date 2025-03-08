package com.example.cafe.log.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.cafe.search.model.CafeDTO;
import com.example.cafe.search.model.CafeImageDTO;

@Repository
public class CfMemberDAOImpl implements CfMemberDAO {

	@Autowired
	SqlSession session;
	
	@Override
	public String login(CfAdminDTO adDto, CfManagerDTO mgDto, CfUserDTO uDto, String option) {

		String result = null;
		try {
	        
			
			 System.out.println("로그인 시도 - option: " + option);
			 System.out.println("CfAdminDTO: " + adDto);
			 System.out.println("CfManagerDTO: " + mgDto);
			 System.out.println("CfUserDTO: " + uDto);
			 
	        
            switch (option) {
                case "admin":
                    result = session.selectOne("log.adminLogin", adDto );
                    break;
                case "manager":
                    result = session.selectOne("log.managerLogin", mgDto);
                    break;
                case "user":
                    result = session.selectOne("log.userLogin", uDto);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid option: " + option);
			}
            
            System.out.println("로그인 결과: " + result);
            return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void join(CfManagerDTO mgdto, CfUserDTO udto, String option) {
		// TODO Auto-generated method stub
		try {
			switch (option) {
            case "manager":
                // cf_adr2 값 명시적으로 설정
                if (mgdto.getCf_adr2() == null || mgdto.getCf_adr2().isEmpty()) {
                	mgdto.setCf_adr2(""); // 빈 문자열로 설정
                }
                session.insert("log.managerJoin", mgdto);
                break;

            case "user":
                session.insert("log.userJoin", udto);
                break;

            default:
                throw new IllegalArgumentException("Invalid option: " + option);
			}
		} catch (Exception e) {
			e.printStackTrace();
            throw new RuntimeException("회원가입 중에 오류가 발생했습니다: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean isIdExist(String userid) {
        try {
            int count = session.selectOne("log.IdExistCount", userid);
            System.out.println("userid Count: " + count);
            return count > 0;
        } catch (Exception e) {
        	e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("아이디 존재 여부 확인 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("아이디 확인 중에 오류 발생", e);
        }
	}
	
	
	@Override
	public String checkPasswd(String userid, int user_au_lv) {
		try {
            Map<String, Object> params = new HashMap<>();
            params.put("userid", userid);
            params.put("user_au_lv", user_au_lv);

            // 기존 비밀번호 조회
            return session.selectOne("log.checkPasswd", params);
			
		} catch (Exception e) {
			e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("패스워드 확인 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("패스워드 확인 중에 오류 발생", e);
		}
	}
	
	@Override
	public String encryptPasswd(Map<String, Object> params) {
		try {
			return session.selectOne("log.encryptPasswd", params);		
		} catch (Exception e) {
			e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("패스워드 확인 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("패스워드 확인 중에 오류 발생", e);
		}
	}
	

	@Override
	public List<Map<String, Object>> findIds(String name, String tel, String email) {
    	try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("name", name);
			params.put("tel", tel);
			params.put("email", email);
			List<Map<String, Object>> userids = session.selectList("log.findIds", params);
			System.out.println("조회된 유저 리스트: " + userids);
			return session.selectList("log.findIds", params);
			
			
		} catch (Exception e) {
			e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("아이디 찾는 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("아이디 찾는 중에 오류 발생", e);
		}
	}

	@Override
	public Map<String, Object> findPasswds(String userid, String name, String tel, String email) {
    	try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("userid", userid);
			params.put("name", name);
			params.put("tel", tel);
			params.put("email", email);
			
			Map<String, Object> userpwd = session.selectOne("log.findPasswds", params);
			System.out.println("조회된 유저 리스트: " + userpwd);
			
			return session.selectOne("log.findPasswds", params);
		} catch (Exception e) {
			e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("비밀번호 찾는 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("비밀번호 찾는 중에 오류 발생", e);
		}
	}
	

	@Override
	public void updateUserPassword(@Param("userid") String userid, 
	                               @Param("passwd") String encryptedPassword, 
	                               @Param("auLv") int auLv) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("userid", userid);
	    params.put("passwd", encryptedPassword);  // 기존 passwd -> encryptedPassword로 변경
	    params.put("auLv", auLv);  

	    session.update("log.updateUserPassword", params);
	}

	
	@Override
	public int editMemberInfo(Map<String, Object> params) {
	    try {
	        // MyBatis 매퍼 호출하여 회원정보 업데이트
	        int result = session.update("log.editMember", params);

	        // 업데이트된 행이 없을 경우 예외 처리
	        if (result == 0) {
	            throw new RuntimeException("회원 정보 업데이트에 실패했습니다.");
	        }

	        return result; // 업데이트된 행 개수 반환

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("수정 중에 오류가 발생했습니다: " + e.getMessage(), e);
	    }
	}



	@Override
	public CfManagerDTO getCafe(String mguserid) {
        try {
			/* System.out.println("DAO:"+session.selectOne("log.getCafe", mguserid)); */
            return session.selectOne("log.getCafe", mguserid);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("카페 정보를 조회하는 중에 오류가 발생했습니다.", e);
        }
	}

	@Override
	public boolean isCodeExist(String cf_code) {
        try {
            int count = session.selectOne("log.isCodeExist", cf_code);
            System.out.println("cf_code Count: " + count);
            return count > 0;
        } catch (Exception e) {
        	e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("사업자번호 중복 찾는 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("사업자번호 중복 찾는 중에 오류 발생", e);
		}
	}

	@Override
	public boolean isCafeRegistered(String userid) {
        try {
            int count = session.selectOne("log.isCafeRegistered", userid);
            System.out.println("userid: " + userid);
            return count > 0;
        } catch (Exception e) {
        	e.printStackTrace();
            // 예외 발생 시 로깅 또는 처리
            System.err.println("카페 등록 중에 오류 발생: " + e.getMessage());
            throw new RuntimeException("카페 등록 중에 오류 발생", e);
		}
	}

	@Override
	public boolean insertCafeInfo(CafeDTO cfDto) {
        try {
            int rows = session.insert("log.insertCafeInfo", cfDto); // SQL 매퍼의 insertCafe 호출
            return rows > 0; // 삽입 성공 여부 반환
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("입력 중에 오류가 발생했습니다: " + e.getMessage(), e);
        }
	}

	@Override
	public void updateCafe(Map<String, Object> params) {
        try {
            session.update("log.updateCafe", params);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("업데이트 중에 오류가 발생했습니다: " + e.getMessage(), e);
        }
	}

	@Override
	public List<CafeImageDTO> getImagesByCfNum(int cf_num) {
        try {
            return session.selectList("log.getImagesByCfNum", cf_num);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("이미지 정보를 가져오는 중에 오류가 발생했습니다.", e);
        }
	}

	@Override
	public CfAdminDTO getAdminInfo(String userid) {
        try {
            return session.selectOne("log.getAdminInfo", userid);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("회원 정보를 가져오는 중에 오류가 발생했습니다.", e);
        }
	}

	@Override
	public CfManagerDTO getManagerInfo(String userid) {
        try {
            return session.selectOne("log.getManagerInfo", userid);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("회원 정보를 가져오는 중에 오류가 발생했습니다.", e);
        }
	}

	@Override
	public CfUserDTO getUserInfo(String userid) {
        try {
            return session.selectOne("log.getUserInfo", userid);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("회원 정보를 가져오는 중에 오류가 발생했습니다.", e);
        }
	}
	
	@Override
	public int saveCafeNum(String userid) {
	    try {
	        Integer cf_number = session.selectOne("log.saveCafeNum", userid);
	        return cf_number != null ? cf_number : -1; // cf_number가 null이면 기본값 -1 반환
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("정보를 가져오는 중에 오류가 발생했습니다.", e);
	    }
	}
	
	public int getUserLevel(String userid) {
	    Integer level = session.selectOne("log.getUserLevel", userid);
	    return (level != null) ? level : -1; // 기본값 -1 (에러 방지)
	}
}
