package com.example.cafe.myFavorite.model;

import java.util.List;

public interface MyFavoriteDAO {
	
	List<MyFavoriteDTO> listMyFavorite(String userid);
	
	String insertFavorite(MyFavoriteDTO dto);
	
	String deleteFavorite(long cf_number, String userid);
	 
	int checkFavorite(long cf_number, String userid);
	
	List<Integer> getMyFavoriteId(String userid);
	
}
