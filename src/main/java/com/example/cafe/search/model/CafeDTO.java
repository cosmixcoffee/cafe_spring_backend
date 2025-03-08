package com.example.cafe.search.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class CafeDTO {
	private int cf_number;
	private String userid;
	private String cf_name;
	private String cf_code;
	private String cf_pcode;
	private String cf_adr1;
	private String cf_adr2;
	private String cf_tel;
	private String cf_content;
	private String cf_map;
	private int cf_visit_num;
	private int cf_rv_count;
	private int cf_point;
}
