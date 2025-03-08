package com.example.cafe.memo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MemoDAOImpl implements MemoDAO {
	
	@Autowired
	SqlSession session;

	@Transactional
	@Override
	public List<MemoDTO> listMemo(String userid, long cf_number) {
		// TODO Auto-generated method stub
		if(cf_number == 0) {
			List<MemoDTO> list = session.selectList("memo.listAllMemo",userid);
			System.out.println("MemoDAO 에서 XML을 통해 전달 받은 리스트 : "+list);
			return list;
		}else {
			//id, cf_number 두 값을 전달해야하기 때문에 map생성
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userid", userid);
			map.put("cf_number", cf_number);
			List<MemoDTO> list = session.selectList("memo.listCfMemo", map);
			System.out.println("MemoDAO 에서 XML을 통해 전달 받은 리스트 : "+list);
			return list;
		}
	}
	@Transactional
	@Override
	public String deleteMemo(long memo_idx) {
		// TODO Auto-generated method stub
		System.out.println("MemoDAO 에서 받온 메모 인덱스 : "+ memo_idx);
		session.delete("memo.deleteMemo",memo_idx);
		System.out.println("삭제되었습니다!");
		//후에 jsp에서 확인 하기 위한 status
		return "deleted";
	}
	
	@Transactional
	@Override
	public String insertMemo(MemoDTO dto) {
		System.out.println("MemoDAO 에서 받은 DTO : " + dto);
		session.insert("memo.insertMemo",dto);
		//후에 jsp에서 확인 하기 위한 status
		return "added";
	}

	@Override
	public MemoDTO detailMemo(long memo_idx) {
		return session.selectOne("memo.detailMemo", memo_idx);
	}

}
