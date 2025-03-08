package com.example.cafe.review.model;

import java.util.List;

public interface ReviewDAO{
	public void insertReview(ReviewDTO dto);
	
	public void deleteReview(int rv_idx);
	
	public List<ReviewDTO> listAllReview(int cf_number, int start, int end);
	
	public List<ReviewDTO> listReview(int cf_number, String userid, int start, int end);
		
	public int countAllReviews(int cf_number);
	
	public int countUserReviews(int cf_number, String userid);
	
	public void updatePoint(int cf_number);		
	
	public void updateCount(int cf_number);
	
	double getCafePoint(int cf_number);
	
}
