package com.example.cafe.search.api;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.cafe.common.PageUtil;
import com.example.cafe.review.model.ReviewDAO;
import com.example.cafe.review.model.ReviewDTO;
import com.example.cafe.search.model.CafeImageDTO;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import com.example.cafe.search.model.CafeDAO;
import com.example.cafe.search.model.CafeDTO;






@RestController
@RequestMapping("/api/cafesearch/*")
public class CafeAPI {
	
	@Autowired
	CafeDAO cafeDao;
	
	@Autowired
	ReviewDAO reviewDao;
 
	@PostMapping("search_list")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> search_list(@RequestParam(name="searchkey", defaultValue = "") String searchkey, 
			@RequestParam(name="search", defaultValue = "") String search,
			@RequestParam(name = "cur_page", defaultValue = "1") int cur_page) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			if (!List.of("cf_name", "cf_adr1", "cf_content", "all").contains(searchkey)) {
				throw new IllegalArgumentException("Invalid search key");
			}
			
			//검색된 카페의 총수
			int count = cafeDao.search_count(searchkey, search);
			
			//페이징 처리
			PageUtil page = new PageUtil(count, cur_page);
			int start = page.getPageBegin();
			int end = page.getPageEnd();

			//검색페이지 가져오기 
			List<CafeDTO> list = cafeDao.list_search(searchkey, search, start, end);
			
	        //결과를 ModelAndView에 저장 (세션 대신 사용)
			response.put("count", count);
			response.put("list", list);
			response.put("searchkey", searchkey);
			response.put("search", search);
			response.put("page", page);
			response.put("message", "success");
	        
			/* System.out.println(list); */
	        
	        return ResponseEntity.ok(response);
			
        } catch (IllegalArgumentException e) {
            response.put("errorMessage", "유효하지 않은 검색 키입니다.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("errorMessage", "데이터 조회 중 문제가 발생했습니다.");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(response);
        }
	}
	
	@GetMapping("cafe_detail")
	public ResponseEntity<Map<String, Object>> cafe_detail(@RequestParam(name="cf_number", defaultValue = "-1") int cf_number) {
	    Map<String, Object> response = new HashMap<>();
	    
		if ( cf_number == -1 ) {
			response.put("status", "nonumber");
	        return ResponseEntity.badRequest().body(response);
	    }
	    
		Map<String, Object> cafeData = cafeDao.detailCafeWithImages(cf_number);
	    if (cafeData.isEmpty() || cafeData.get("cafeInfo") == null) {
	    	response.put("errorMessage", "카페 정보를 찾을 수 없습니다");
	        return ResponseEntity.notFound().build();
	    }
	    
	    // 이미지 정보 조회
	    CafeImageDTO cafeImages = (CafeImageDTO) cafeData.get("cafeImages");
	    if (cafeImages == null) {
	        System.out.println("DB에서 cf_number " + cf_number + "에 대한 이미지가 없음");
	        cafeImages = new CafeImageDTO();
	    }
	    
	    String basePath = "/resources/images/";
	    List<String> subImagePaths = new ArrayList<>();
	    
	    if (cafeImages.getSubfilename1() != null) subImagePaths.add(basePath + cafeImages.getSubfilename1());
	    if (cafeImages.getSubfilename2() != null) subImagePaths.add(basePath + cafeImages.getSubfilename2());
	    if (cafeImages.getSubfilename3() != null) subImagePaths.add(basePath + cafeImages.getSubfilename3());
	    if (cafeImages.getSubfilename4() != null) subImagePaths.add(basePath + cafeImages.getSubfilename4());
	    
	    String mainImagePath = (cafeImages.getMainfilename() != null) ? basePath + cafeImages.getMainfilename() : null;
	    
        response.put("cafeInfo", cafeData.get("cafeInfo"));
        response.put("cafeImages", cafeImages);
        response.put("mainImagePath", mainImagePath);
        response.put("subImagePaths", subImagePaths);
        
        return ResponseEntity.ok(response);
	}
	

	
	//삭제 관련 
    @DeleteMapping("deleteSelected")
    
    public ResponseEntity<Map<String, String>> deleteSelected(@RequestParam("ids") String ids) {
        Map<String, String> response = new HashMap<>();
    	
        if (ids == null || ids.trim().isEmpty()) {
    		response.put("message", "fail");
            return ResponseEntity.badRequest().body(response);
        }
        
        String[] idArray = ids.split(",");
        try {
            for (String id : idArray) {
                int cf_number = Integer.parseInt(id.trim());
                cafeDao.selectdelete(cf_number); // DAO 메서드를 호출하여 삭제 처리
            }
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 로깅 프레임워크(SLF4J 등)를 사용하면 더욱 좋습니다.
            e.printStackTrace();
            response.put("message", "fail");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 메모 
    @GetMapping("listAllReview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listAllReview(
            @RequestParam(name = "cf_number", defaultValue = "-1") int cf_number,
            @RequestParam(name = "cur_page", defaultValue = "1") int cur_page) {

        Map<String, Object> response = new HashMap<>();

        if (cf_number == -1) {
            response.put("status", "error");
            response.put("message", "유효하지 않은 카페 번호입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            int totalReviews = reviewDao.countAllReviews(cf_number);

            PageUtil page = new PageUtil(totalReviews, cur_page);
            int start = page.getPageBegin();
            int end = page.getPageEnd();

            List<ReviewDTO> list = reviewDao.listAllReview(cf_number, start, end);

            response.put("status", "success");
            response.put("total", totalReviews);
            response.put("cur_page", cur_page);
            response.put("page", page);
            response.put("reviewList", list);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "후기 데이터를 가져오는 중 오류 발생.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 메모삭제 
    @DeleteMapping("deleteReview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteReview(@RequestBody Map<String, List<Integer>> requestBody) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Integer> rv_idxList = requestBody.get("rv_idx"); // JSON에서 배열 추출

            if (rv_idxList == null || rv_idxList.isEmpty()) {
                response.put("status", "error");
                response.put("message", "삭제할 후기를 선택하세요.");
                return ResponseEntity.badRequest().body(response);
            }

            for (int rv_idx : rv_idxList) {
                reviewDao.deleteReview(rv_idx);
            }
            response.put("status", "success");
            response.put("message", "선택한 후기가 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "리뷰 삭제 실패: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
    
    //메모 입력 
    @PostMapping("insertReview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> insertReview(
            @RequestParam(name="cf_number", defaultValue = "-1") int cf_number,
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
            return ResponseEntity.badRequest().body(response);
        }

        if (rv_content == null || rv_content.trim().isEmpty()) {
            rv_content = "내용 없음";  // 기본 메시지 설정
        }

        // 파일 저장 관련
        ServletContext application = request.getSession().getServletContext();
        String uploadPath = application.getRealPath("/resources/imgReviews/");
        new File(uploadPath).mkdirs();

        if (rv_filepath != null && !rv_filepath.isEmpty()) {
            try {
                String savedFileName = saveImage(uploadPath, rv_filepath);
                reviewDto.setRv_filename(savedFileName);
            } catch (IOException e) {
                System.out.println("❌ 파일 저장 실패: " + e.getMessage());
                response.put("status", "error");
                response.put("message", "파일 업로드 실패");
                return ResponseEntity.internalServerError().body(response);
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
            return ResponseEntity.internalServerError().body(response);
        }

        // JSON 응답에서 `rv_filepath` 제외
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("rv_idx", reviewDto.getRv_idx());
        reviewData.put("cf_number", reviewDto.getCf_number());
        reviewData.put("userid", reviewDto.getUserid());
        reviewData.put("rv_content", reviewDto.getRv_content());
        reviewData.put("rv_filename", reviewDto.getRv_filename());
        reviewData.put("w_date", reviewDto.getW_date());
        reviewData.put("cf_point", reviewDto.getCf_point());

        response.put("status", "success");
        response.put("message", "리뷰가 성공적으로 추가되었습니다.");
        response.put("review", reviewData);
        response.put("updatedReviewCount", updatedCount); // ✅ 최신 리뷰 개수 반환
        response.put("updatedPoint", updatedPoint); 

        return ResponseEntity.ok(response);
    }

    // 파일 저장 메서드
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



}
