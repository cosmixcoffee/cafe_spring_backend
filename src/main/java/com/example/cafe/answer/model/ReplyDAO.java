package com.example.cafe.answer.model;

import java.util.List;

public interface ReplyDAO {
	List<ReplyDTO> adminList(int board_idx, int start, int end);
	List<ReplyDTO> manager_jsonList(int board_idx, int start, int end, String userid);
	List<ReplyDTO> managerList(int board_idx, int start, int end);
	List<ReplyDTO> user_jsonList(int board_idx, int start, int end, String userid);
	List<ReplyDTO> userList(int board_idx, int start, int end);
    
	int count(int board_idx);
	void insert(ReplyDTO dto);
	void update(ReplyDTO dto);
	void delete(int idx);
	ReplyDTO detail(int idx);
}
