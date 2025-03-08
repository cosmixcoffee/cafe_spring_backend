package com.example.cafe.myFavorite.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MyFavoriteDAOImpl implements MyFavoriteDAO {
	
	@Autowired
	SqlSession session;
	
	@Transactional
	@Override
	public List<MyFavoriteDTO> listMyFavorite(String userid) {
		List<MyFavoriteDTO> list = session.selectList("myFavorite.list_myFavorite",userid);
		return list;
	}
	
	@Transactional
	@Override
	public String insertFavorite(MyFavoriteDTO dto) {
		System.out.println("추가!");
		session.insert("myFavorite.insert_myFavorite", dto);
		return "added";
	}

	@Transactional
	@Override
	public String deleteFavorite(long cf_number, String userid) {
		System.out.println("삭제!");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cf_number", cf_number);
		map.put("userid", userid);
		System.out.println("맵 : " + map);
		session.delete("myFavorite.delete_myFavorite", map);
		return "deleted";
	}

	@Override
	public int checkFavorite(long cf_number, String userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cf_number", cf_number);
		map.put("userid", userid);
		int count = session.selectOne("myFavorite.check_myFavorite", map);
		return count;
	}

	@Transactional
	@Override
	public List<Integer> getMyFavoriteId(String userid) {
		List<Integer> favoriteIds = session.selectList("myFavorite.get_myFavorite",userid);	
		return favoriteIds;
	}

}
