package com.example.cafe.search.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class CafeImageDTO {
    private int img_idx;        // 이미지 번호
    private int cf_number;         // 카페 번호
    
    private String mainfilename;
    private MultipartFile main_img_path; // 메인 이미지 파일 경로

    private String subfilename1;
    private MultipartFile sub_img1_path; // 서브 이미지 1 파일 경로
    
    private String subfilename2;
    private MultipartFile sub_img2_path; // 서브 이미지 2 파일 경로
    
    private String subfilename3;
    private MultipartFile sub_img3_path; // 서브 이미지 3 파일 경로
   
    private String subfilename4;
    private MultipartFile sub_img4_path; // 서브 이미지 4 파일 경로
}
