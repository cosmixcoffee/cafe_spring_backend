package com.example.cafe.log.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CfManagerDTO {
	private String userid;
	private String passwd;
	private String name;
	private String email;
	private String tel;
	private int au_lv;
	private String cf_name;
	private String cf_pcode;
	private String cf_adr1;
	private String cf_adr2;
	private String cf_tel;
	
}
