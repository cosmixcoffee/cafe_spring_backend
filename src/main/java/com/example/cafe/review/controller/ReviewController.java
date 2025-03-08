package com.example.cafe.review.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.cafe.common.PageUtil;
import com.example.cafe.review.model.ReviewDAO;
import com.example.cafe.review.model.ReviewDTO;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController{
	
	@Autowired
	ReviewDAO reviewDao;
	
	
	@GetMapping("listAllReview.do")
	public String listAllReview(
	        @RequestParam(name = "cf_number", defaultValue = "-1") int cf_number,
	        @RequestParam(name = "cur_page", defaultValue = "1") int cur_page,
	        Model model) {

	    int totalReviews = reviewDao.countAllReviews(cf_number);

	    PageUtil page = new PageUtil(totalReviews, cur_page);
	    int start = page.getPageBegin();
	    int end = page.getPageEnd();

	    List<ReviewDTO> list = reviewDao.listAllReview(cf_number, start, end);

	    model.addAttribute("status", "success");
	    model.addAttribute("total", totalReviews);
	    model.addAttribute("cur_page", cur_page);
	    model.addAttribute("page", page);
	    model.addAttribute("reviewList", list);

	    return "/review/review";  // review.jsp로 이동
	}

	
	@GetMapping("listReview.do")
	@ResponseBody
	public Map<String, Object> listReview(@RequestParam(name="cf_number", defaultValue = "-1") int cf_number,
			@RequestParam(name="userid", required =false) String userid,
			@RequestParam(name = "cur_page", defaultValue = "1") int cur_page) {
		
		Map<String, Object> response = new HashMap<>();
		
		if (userid == null || userid.trim().isEmpty()) {
	        response.put("status", "error");
	        response.put("message", "로그인이 필요한 기능입니다.");
	        return response;
	    }
		// 페이징 추가 
		int totalReviews = reviewDao.countUserReviews(cf_number, userid);
		
		PageUtil page = new PageUtil(totalReviews, cur_page);
		int start = page.getPageBegin();
		int end = page.getPageEnd();
		
		List<ReviewDTO> list = reviewDao.listReview(cf_number, userid, start, end); 
		response.put("status", "error");
		response.put("total", totalReviews);
		response.put("cur_page", cur_page);
		response.put("page", page);
		response.put("data", list);
		System.out.println("DAO에서 받은 리스트: " + list);
		return response;
	}
	
	@RequestMapping("insertReview.do")
	@ResponseBody
	public Map<String, Object> insertReview(@RequestParam(name="cf_number", defaultValue = "-1") int cf_number,
			@RequestParam(name="userid", required =false) String userid,
			@RequestParam(name="rv_content", required = false) String rv_content,
			@RequestParam(value = "rv_filename", required = false) MultipartFile rv_filepath,
			@RequestParam(name="cf_point", defaultValue = "0") int cf_point,
			HttpServletRequest request) {	    
		
		Map<String, Object> response = new HashMap<>();
		ReviewDTO reviewDto = new ReviewDTO();
		
	    
		if (userid == null || userid.trim().isEmpty()) {
	        response.put("status", "error");
	        response.put("message", "로그인이 필요한 기능입니다.");
	        return response;
	    }
		
	    if (rv_content == null || rv_content.trim().isEmpty()) {
	        rv_content = "내용 없음";  // 기본 메시지 설정
	    }
		
	    
	    
		//파일 저장관련
        ServletContext application = request.getSession().getServletContext();
        String uploadPath = application.getRealPath("/resources/imgReviews/");
        new File(uploadPath).mkdirs();
        
        if (rv_filepath != null && !rv_filepath.isEmpty()) {
            try {
                String savedFileName = saveImage(uploadPath, rv_filepath);
                reviewDto.setRv_filename(savedFileName);
                reviewDto.setRv_filepath(rv_filepath);
            } catch (IOException e) {
                System.out.println("❌ 파일 저장 실패: " + e.getMessage());
                response.put("status", "error");
                response.put("message", "파일 업로드 실패");
                return response;
            }
        } else {
            reviewDto.setRv_filename(""); // 파일이 없을 경우 빈 문자열 저장
        }

        
       // 리뷰 정보 저장
        reviewDto.setCf_number(cf_number);
        reviewDto.setUserid(userid);
        reviewDto.setRv_content(rv_content);
        reviewDto.setCf_point(cf_point);
        reviewDto.setW_date(new Date());
        
        int updatedCount = 0;
        double updatedPoint = 0.0; 
        
        // 리뷰 저장 & 카페 평점 업데이트
        try {
            reviewDao.insertReview(reviewDto);
            System.out.println("[insertReview] DB 저장 완료: " + reviewDto);
            
            reviewDao.updatePoint(cf_number);
            reviewDao.updateCount(cf_number);
            System.out.println("[insertReview] 카페 평점 및 리뷰 개수 업데이트 완료");
            updatedCount = reviewDao.countAllReviews(cf_number);
            updatedPoint = reviewDao.getCafePoint(cf_number); 
        } catch (Exception e) {
            System.out.println("[insertReview] DB 저장 실패: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "DB 저장 실패");
            return response;
        }
        
        // JSON 응답에서 `rv_filepath` 제외
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("rv_idx", reviewDto.getRv_idx());
        reviewData.put("cf_number", reviewDto.getCf_number());
        reviewData.put("userid", reviewDto.getUserid());
        reviewData.put("rv_content", reviewDto.getRv_content());
        reviewData.put("cf_name", reviewDto.getCf_name());
        reviewData.put("rv_filename", reviewDto.getRv_filename());
        reviewData.put("w_date", reviewDto.getW_date());
        reviewData.put("cf_point", reviewDto.getCf_point());


        response.put("status", "success");
        response.put("message", "리뷰가 성공적으로 추가되었습니다.");
        response.put("review", reviewData);
        response.put("updatedReviewCount", updatedCount); // ✅ 최신 리뷰 개수 반환
        response.put("updatedPoint", updatedPoint); 

        return response;
    }
	
	//저장 메소드 (파일 저장 로직을 따로 분리)
	private String saveImage(String uploadPath, MultipartFile imageFile) throws IOException {
	    if (imageFile == null || imageFile.isEmpty()) {
	        throw new IOException("업로드된 파일이 없습니다.");
	    }

	    String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
	    File image = new File(uploadPath, fileName);

	    try (InputStream in = imageFile.getInputStream();
	         OutputStream out = new FileOutputStream(image)) {
	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = in.read(buffer)) != -1) {
	            out.write(buffer, 0, bytesRead);
	        }
	    }

	    return fileName;
	}
	
	
    // 리뷰 삭제
	@DeleteMapping("/deleteReview.do")
    public Map<String, Object> deleteReview(@RequestParam("rv_idx") int rv_idx) {
        Map<String, Object> response = new HashMap<>();

        try {
            reviewDao.deleteReview(rv_idx);
            response.put("status", "success");
            response.put("message", "리뷰가 삭제되었습니다.");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "리뷰 삭제 실패: " + e.getMessage());
        }

        return response;
    }
    
}
