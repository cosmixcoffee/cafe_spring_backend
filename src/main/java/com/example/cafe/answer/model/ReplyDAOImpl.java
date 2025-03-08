package com.example.cafe.answer.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
@Repository
public class ReplyDAOImpl implements ReplyDAO {
	
	@Autowired
	SqlSession sqlSession;
	@Override
	public List<ReplyDTO> adminList(int board_idx, int start, int end) {
	    return sqlSession.selectList("reply.adminList",
	            Map.of("board_idx", board_idx, "start", start, "end", end));
	}
	@Override
	public List<ReplyDTO> manager_jsonList(int board_idx, int start, int end, String userid) {
	    if (userid == null || userid.isEmpty()) {
	        throw new IllegalArgumentException("관리자의 댓글 조회에서는 userid가 필요합니다.");
	    }
	    return sqlSession.selectList("reply.managerList",
	            Map.of("board_idx", board_idx, "start", start, "end", end, "replyer", userid));
	}
	
	@Override
	public List<ReplyDTO> managerList(int board_idx, int start, int end) {
	    Map<String, Object> map = new HashMap<>();
	    map.put("start", start);
	    map.put("end", end);
	    map.put("board_idx", board_idx);
	    return sqlSession.selectList("reply.managerList", map);
	}
	
	
	@Override
	public List<ReplyDTO> user_jsonList(int board_idx, int start, int end, String userid) {
	    if (userid == null || userid.isEmpty()) {
	        throw new IllegalArgumentException("사용자의 댓글 조회에서는 userid가 필요합니다.");
	    }
	    return sqlSession.selectList("reply.userList",
	            Map.of("board_idx", board_idx, "start", start, "end", end, "replyer", userid));
	}
	
	@Override
	public List<ReplyDTO> userList(int board_idx, int start, int end) {
	    Map<String, Object> map = new HashMap<>();
	    map.put("start", start);
	    map.put("end", end);
	    map.put("board_idx", board_idx);
	    return sqlSession.selectList("reply.userList", map);
	}


	@Override
	public int count(int board_idx) {
		return sqlSession.selectOne("reply.count", board_idx);
	}

	@Override
	public void insert(ReplyDTO dto) {
		sqlSession.insert("reply.insert", dto);

	}

	@Override
	public void update(ReplyDTO dto) {
		sqlSession.update("reply.update", dto);

	}

	@Override
	public void delete(int idx) {
		sqlSession.delete("reply.delete", idx);

	}

	@Override
	public ReplyDTO detail(int idx) {
		return sqlSession.selectOne("reply.detail", idx);
	}

}
