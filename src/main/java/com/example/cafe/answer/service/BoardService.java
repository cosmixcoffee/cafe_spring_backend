package com.example.cafe.answer.service;

import java.util.List;

import com.example.cafe.answer.model.BoardDTO;



public interface BoardService {
    List<BoardDTO> adminList(int start, int end, String search_option, String keyword);
    List<BoardDTO> managerList(int start, int end, String search_option, String keyword);
    List<BoardDTO> userList(int start, int end, String search_option, String keyword);
	
	void insert(BoardDTO dto);
	
	BoardDTO adminDetail(int idx);
	BoardDTO managerDetail(int idx);
	BoardDTO userDetail(int idx);

	
	void increase_hit(int idx);
	
	void update(BoardDTO dto);
	
	void delete(int idx);
	
	/* int count(String search_option, String keyword); */
	int adminCount(String search_option, String keyword);
	int managerCount(String search_option, String keyword);
	int userCount(String search_option, String keyword);
	
	List<String> list_attach(int idx);
	
	void delete_attach(String file_name);
	
	void update_attach(String file_name, int idx);
}
