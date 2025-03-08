package com.example.cafe.answer.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplyDTO {
	private int idx;
	private Integer board_idx;
	private String reply_text;
	private String replyer;
	private String name;
	private Date regdate;
	private String writer;

}
