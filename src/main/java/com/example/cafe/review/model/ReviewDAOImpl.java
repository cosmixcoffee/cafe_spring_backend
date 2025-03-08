package com.example.cafe.review.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewDAOImpl implements ReviewDAO {
	
	@Autowired
	SqlSession session;
	
	
		
	@Override
	public void insertReview(ReviewDTO dto) {
		session.insert("review.insertReview",dto);
	}

	@Override
	public void deleteReview(int rv_idx) {
		session.delete("review.deleteReview",rv_idx);

	}

	@Override
	public List<ReviewDTO> listAllReview(int cf_number, int start, int end) {
		Map<String, Object> params = new HashMap<>();
		params.put("cf_number", cf_number);
		params.put("start", start);
		params.put("end", end);
		System.out.println("전달받은 리스트: " + params);
		return session.selectList("review.listAllReview",params);
	}
	
	@Override
	public List<ReviewDTO> listReview(int cf_number, String userid, int start, int end) {
		Map<String, Object> params = new HashMap<>();
		params.put("cf_number", cf_number);
		params.put("userid", userid);
		params.put("start", start);
		params.put("end", end);
		List<ReviewDTO> list = session.selectList("review.listReview",params);
		return list;
	}
	

	@Override
	public int countAllReviews(int cf_number) {
	    return session.selectOne("review.countAllReviews", cf_number);
	}
	
	@Override
	public int countUserReviews(int cf_number, String userid) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("cf_number", cf_number);
	    params.put("userid", userid);
	    return session.selectOne("review.countUserReviews", params);
	}

	@Override
	public void updatePoint(int cf_number) {
		session.update("updatePoint",cf_number);
	}

	@Override
	public void updateCount(int cf_number) {
		session.update("updateCount",cf_number);
	}
	
	@Override
	public double getCafePoint(int cf_number) {
	    return session.selectOne("review.getCafePoint", cf_number);
	}
}
