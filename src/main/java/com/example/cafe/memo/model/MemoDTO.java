package com.example.cafe.memo.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoDTO {
	String userid;
	long memo_idx;
	long cf_number;
	String memo_content;
	byte[] memo_file;
	LocalDate w_date;
	String memo_title;
	String cf_name;
}
