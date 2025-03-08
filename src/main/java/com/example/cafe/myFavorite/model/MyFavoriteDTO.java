package com.example.cafe.myFavorite.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyFavoriteDTO {
	private long fav_idx;
	private String userid;
	private long cf_number;
	private String cf_name;
}
