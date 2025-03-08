package com.example.cafe.log.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CfUserDTO {
	private String userid;
	private String passwd;
	private String name;
	private String email;
	private String tel;
	private int au_lv;
}
