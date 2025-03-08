package com.example.cafe.review.model;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class ReviewDTO {
	private int rv_idx;
	private int cf_number;
	private String userid;
	private String rv_content;
	private String cf_name;
	private String rv_filename;
	
    @JsonIgnore // JSON 응답에서 제외
    private MultipartFile rv_filepath;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date w_date = new Date();
	private int cf_point;
	
	
	
}
