package com.example.cafe.search.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.cafe.common.PageUtil;
import com.example.cafe.search.model.CafeImageDTO;
import com.example.cafe.search.model.CafeDAO;
import com.example.cafe.search.model.CafeDTO;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;




@Controller
/* @RequestMapping("/cafe/cafesearch/*") */
public class CafeController {
	
	@Autowired
	CafeDAO cafeDao;
 
	@GetMapping("search_list.do")
	@ResponseBody
	public ModelAndView search_list(@RequestParam(name="searchkey", defaultValue = "") String searchkey, 
			@RequestParam(name="search", defaultValue = "") String search,
			@RequestParam(name = "cur_page", defaultValue = "1") int cur_page, ModelAndView mav) {
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
			mav.setViewName("/search/cf_search");
			mav.addObject("count", count);
	        mav.addObject("list", list);
	        mav.addObject("searchkey", searchkey);
	        mav.addObject("search", search);
	        mav.addObject("page", page);
	        mav.addObject("message", "success");
	        
	        System.out.println(list);
			
		} catch (IllegalArgumentException e) {
			// 검색 키 검증 실패 시 처리
            mav.setViewName("error_page");
            mav.addObject("errorMessage", "유효하지 않은 검색 키입니다.");
		} catch (Exception e) {
            // 기타 예외 처리
            mav.setViewName("error_page");
            mav.addObject("errorMessage", "데이터 조회 중 문제가 발생했습니다.");
            e.printStackTrace(); // 로그 출력
		}        
		return mav;
	}
	
	@GetMapping("cafe_detail.do")
	public String cafe_detail(@RequestParam(name="cf_number", defaultValue = "-1") int cf_number, Model model) {
	    
		if ( cf_number == -1 ) {
			 model.addAttribute("status", "nonumber");
		     model.addAttribute("redirect", "/include/cf_detail_error.jsp");
	        return "include/cf_detail_error";
	    }
	    
		Map<String, Object> cafeData = cafeDao.detailCafeWithImages(cf_number);
	    if (cafeData.isEmpty() || cafeData.get("cafeInfo") == null) {
	        model.addAttribute("errorMessage", "카페 정보를 찾을 수 없습니다.");
	        return "include/cf_detail_error";
	    }
	    
	    // 이미지 정보 조회
	    CafeImageDTO cafeImages = (CafeImageDTO) cafeData.get("cafeImages");
	    
	    if (cafeImages == null) {
	        System.out.println("⚠️ DB에서 cf_number " + cf_number + "에 대한 이미지가 없음");
	        cafeImages = new CafeImageDTO();
	    }
	    
	    String basePath = "/resources/images/";
	    List<String> subImagePaths = new ArrayList<>();
	    
	    if (cafeImages.getSubfilename1() != null) subImagePaths.add(basePath + cafeImages.getSubfilename1());
	    if (cafeImages.getSubfilename2() != null) subImagePaths.add(basePath + cafeImages.getSubfilename2());
	    if (cafeImages.getSubfilename3() != null) subImagePaths.add(basePath + cafeImages.getSubfilename3());
	    if (cafeImages.getSubfilename4() != null) subImagePaths.add(basePath + cafeImages.getSubfilename4());
	    
	    String mainImagePath = (cafeImages.getMainfilename() != null) ? basePath + cafeImages.getMainfilename() : null;
	    
	    model.addAttribute("cafeInfo", cafeData.get("cafeInfo"));
	    model.addAttribute("cafeImages", cafeImages);
	    model.addAttribute("mainImagePath", mainImagePath); 
	    model.addAttribute("subImagePaths", subImagePaths); 
	
	    return "search/cf_detail";
	}
	
	
	@GetMapping("cafe_detailcheck.do")
    @ResponseBody
    public Map<String, Object> cafe_detailcheck(@RequestParam(name="userid", required=true) String userid,
    		@RequestParam(name="cf_number", defaultValue = "-1") Integer cf_number) { 	
    	
	 	Map<String, Object> response = new HashMap<>();
	 	
	    //코드 빈칸 체크
	    if (userid == null || userid.trim().isEmpty()) {
	        response.put("status", "error");
	        response.put("message", "로그인을 다시 해주십시오.");
	        response.put("redirect", "/log/login");
	        return response;
	    }
	    
	    //코드 빈칸 체크
	    if (cf_number == null || cf_number == -1 ) {
	        response.put("status", "nonumber");
	        response.put("message", "카페를 등록해주세요.");
	        response.put("redirect", "/mypage/mypage");
	        return response;
	    }
	    
	    try {
	        // 카페 정보 조회
	        CafeDTO cafeInfo = cafeDao.detailCafe(cf_number, userid);

	        if (cafeInfo == null) {
	            response.put("status", "error");
	            response.put("message", "카페 정보를 찾을 수 없습니다.");
	        } else {
	        	response.put("status", "success");
	            response.put("cf_number", cafeInfo.getCf_number()); 
	            response.put("userid", cafeInfo.getUserid());
	            response.put("cf_name", cafeInfo.getCf_name());
	            response.put("cf_adr1", cafeInfo.getCf_adr1());
	            response.put("cf_adr2", cafeInfo.getCf_adr2());
	            response.put("cf_tel", cafeInfo.getCf_tel());
	            response.put("cf_content", cafeInfo.getCf_content());
	            response.put("cf_map", cafeInfo.getCf_map());
	            response.put("cf_visit_num", cafeInfo.getCf_visit_num());
	            response.put("cf_rv_count", cafeInfo.getCf_rv_count());
	            response.put("cf_point", cafeInfo.getCf_point());
	        }
         
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "카페 정보를 조회하는 중 오류가 발생했습니다.");
	    }

	    return response;  // Spring Boot가 자동으로 JSON 변환
	}
	
	// 카페이미지 등록
	@GetMapping("getImages.do")
	@ResponseBody
	public Map<String, Object> getImages(@RequestParam(name="cf_number") int cf_number) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        CafeImageDTO cafeImage = cafeDao.img_info(cf_number);

	        if (cafeImage != null) {
	            Map<String, String> imagePaths = new HashMap<>();
	            String basePath = "/resources/images/"; // 저장된 이미지 경로

	            // null 체크 후 빈 문자열("") 처리
	            imagePaths.put("main_img_path", cafeImage.getMainfilename() != null ? basePath + cafeImage.getMainfilename() : "");
	            imagePaths.put("sub_img1_path", cafeImage.getSubfilename1() != null ? basePath + cafeImage.getSubfilename1() : "");
	            imagePaths.put("sub_img2_path", cafeImage.getSubfilename2() != null ? basePath + cafeImage.getSubfilename2() : "");
	            imagePaths.put("sub_img3_path", cafeImage.getSubfilename3() != null ? basePath + cafeImage.getSubfilename3() : "");
	            imagePaths.put("sub_img4_path", cafeImage.getSubfilename4() != null ? basePath + cafeImage.getSubfilename4() : "");

	            System.out.println("서버에서 보내는 이미지 데이터: " + imagePaths); 

	            response.put("status", "success");
	            response.put("data", imagePaths);
	        } else {
	            response.put("status", "error");
	            response.put("message", "이미지를 등록해주세요.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "이미지 데이터를 가져오는 중 오류 발생: " + e.getMessage());
	    }

	    return response;
	}




	
	// 마이페이지 카페정보 수정 (이미지만)
	@RequestMapping(value = "img_insert.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> img_insert(@RequestParam(name="cf_number") int cf_number,
	                                      @RequestParam(value = "mainImage", required = false) MultipartFile main_img_path,
	                                      @RequestParam(value = "subImage1", required = false) MultipartFile sub_img1,
	                                      @RequestParam(value = "subImage2", required = false) MultipartFile sub_img2,
	                                      @RequestParam(value = "subImage3", required = false) MultipartFile sub_img3,
	                                      @RequestParam(value = "subImage4", required = false) MultipartFile sub_img4,
	                                      HttpServletRequest request) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        System.out.println("요청받은 cf_number: " + cf_number);
	        System.out.println("mainImage 파일명: " + (main_img_path != null ? main_img_path.getOriginalFilename() : "null"));
	        System.out.println("subImage1 파일명: " + (sub_img1 != null ? sub_img1.getOriginalFilename() : "null"));
	        System.out.println("subImage2 파일명: " + (sub_img2 != null ? sub_img2.getOriginalFilename() : "null"));
	        System.out.println("subImage3 파일명: " + (sub_img3 != null ? sub_img3.getOriginalFilename() : "null"));
	        System.out.println("subImage4 파일명: " + (sub_img4 != null ? sub_img4.getOriginalFilename() : "null"));

	        ServletContext application = request.getSession().getServletContext();
	        String uploadPath = application.getRealPath("/resources/images/");
	        new File(uploadPath).mkdirs();

	        CafeImageDTO cafeImageDto = new CafeImageDTO();
	        cafeImageDto.setCf_number(cf_number);

	        // 기존 데이터 삭제 후 초기화 (이전 이미지 유지 X)
	        if (cafeDao.img_count(cf_number) > 0) {
	            cafeDao.delete_images(cf_number); // 👉 기존 데이터 삭제
	        }

	        // 메인 이미지 저장
	        cafeImageDto.setMainfilename(main_img_path != null && !main_img_path.isEmpty() ? saveImage(uploadPath, main_img_path) : "");

	        // 서브 이미지 저장 (없으면 빈 문자열로 설정)
	        cafeImageDto.setSubfilename1(sub_img1 != null && !sub_img1.isEmpty() ? saveImage(uploadPath, sub_img1) : "");
	        cafeImageDto.setSubfilename2(sub_img2 != null && !sub_img2.isEmpty() ? saveImage(uploadPath, sub_img2) : "");
	        cafeImageDto.setSubfilename3(sub_img3 != null && !sub_img3.isEmpty() ? saveImage(uploadPath, sub_img3) : "");
	        cafeImageDto.setSubfilename4(sub_img4 != null && !sub_img4.isEmpty() ? saveImage(uploadPath, sub_img4) : "");

	        // 새로운 데이터 저장
	        cafeDao.img_insert(cafeImageDto);

	        response.put("status", "success");
	        response.put("message", "이미지가 성공적으로 업로드되었습니다.");
	        response.put("mainImage", cafeImageDto.getMainfilename());
	        response.put("subFileNames", Arrays.asList(
            cafeImageDto.getSubfilename1(),
            cafeImageDto.getSubfilename2(),
            cafeImageDto.getSubfilename3(),
            cafeImageDto.getSubfilename4()
	        ));

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "이미지 업로드 중 오류 발생: " + e.getMessage());
	    }

	    return response;
	}

	//이미지 저장 메소드 (파일 저장 로직을 따로 분리)
	private String saveImage(String uploadPath, MultipartFile imageFile) throws IOException {
	    String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
	    File image = new File(uploadPath, fileName);
	    imageFile.transferTo(image);
	    return fileName;
	}
	
	
	@PostMapping("cafeinfoup.do")
	@ResponseBody
	public Map<String, Object> cafeinfoup(@RequestParam(name="cf_number") int cf_number,
			@RequestParam(name="cf_name") String cf_name,
			@RequestParam(name="cf_code") String cf_code,
			@RequestParam(name="cf_pcode") String cf_pcode,
			@RequestParam(name="cf_adr1") String cf_adr1,
			@RequestParam(name="cf_adr2") String cf_adr2,
			@RequestParam(name="cf_tel") String cf_tel,
			@RequestParam(name="cf_content") String cf_content) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("cf_number", cf_number);
			if (cf_name != null && !cf_name.trim().isEmpty()) params.put("cf_name", cf_name);
	        if (cf_code != null && !cf_code.trim().isEmpty()) params.put("cf_code", cf_code);
	        if (cf_pcode != null && !cf_pcode.trim().isEmpty()) params.put("cf_pcode", cf_pcode);
	        if (cf_adr1 != null && !cf_adr1.trim().isEmpty()) params.put("cf_adr1", cf_adr1);
	        if (cf_adr2 != null && !cf_adr2.trim().isEmpty()) params.put("cf_adr2", cf_adr2);
	        if (cf_tel != null && !cf_tel.trim().isEmpty()) params.put("cf_tel", cf_tel);
	        if (cf_content != null && !cf_content.trim().isEmpty()) params.put("cf_content", cf_content);
			
			cafeDao.updateCafe(params);
			
            response.put("status", "success");
	        response.put("message", "카페 정보가 업데이트되었습니다.");
            response.put("data", params);	
		} catch (Exception e) {
			e.printStackTrace();
            response.put("status", "error");
	        response.put("message", "카페 정보가 업데이트되었습니다.");
		}
		return response;
	}
	//삭제 관련 
    @DeleteMapping("deleteSelected.do")
    public ResponseEntity<String> deleteSelected(@RequestParam("ids") String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("fail");
        }
        String[] idArray = ids.split(",");
        try {
            for (String id : idArray) {
                int cf_number = Integer.parseInt(id.trim());
                cafeDao.selectdelete(cf_number); // DAO 메서드를 호출하여 삭제 처리
            }
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            // 로깅 프레임워크(SLF4J 등)를 사용하면 더욱 좋습니다.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
        }
    }

}
