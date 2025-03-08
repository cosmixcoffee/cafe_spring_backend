package com.example.cafe.answer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cafe.answer.model.BoardDAO;
import com.example.cafe.answer.model.BoardDTO;


@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	BoardDAO boardDao;
	
    @Override
    public List<BoardDTO> adminList(int start, int end, String search_option, String keyword) {
        return boardDao.adminList(start, end, search_option, keyword);
    }

    @Override
    public List<BoardDTO> managerList(int start, int end, String search_option, String keyword) {
        return boardDao.managerList(start, end, search_option, keyword);
    }

    @Override
    public List<BoardDTO> userList(int start, int end, String search_option, String keyword) {
        return boardDao.userList(start, end, search_option, keyword);
    }
	
	@Transactional
	@Override
	public void insert(BoardDTO dto) {
		boardDao.insert(dto);
		String[] files = dto.getFiles();
		if (files == null)
			return;
		for (String name : files) {
			boardDao.insert_attach(name);
		}
	}

	@Override
	public BoardDTO adminDetail(int idx) {
	    return boardDao.adminDetail(idx);
	}

	@Override
	public BoardDTO managerDetail(int idx) {
	    return boardDao.managerDetail(idx);
	}

	@Override
	public BoardDTO userDetail(int idx) {
	    return boardDao.userDetail(idx);
	}

	@Override
	public void increase_hit(int idx) {
		boardDao.increase_hit(idx);

	}
	@Transactional
	@Override
	public void update(BoardDTO dto) {
		boardDao.update(dto);
		String[] files = dto.getFiles();
		if (files == null)
			return;
		for (String name : files) {
			boardDao.update_attach(name, dto.getIdx());
		}
	}

	@Override
	public void delete(int idx) {
		boardDao.delete(idx);

	}

	/*
	 * @Override public int count(String search_option, String keyword) { return
	 * boardDao.count(search_option, keyword); }
	 */

    @Override
    public int adminCount(String search_option, String keyword) {
        return boardDao.adminCount(search_option, keyword);
    }

    @Override
    public int managerCount(String search_option, String keyword) {
        return boardDao.managerCount(search_option, keyword);
    }

    @Override
    public int userCount(String search_option, String keyword) {
        return boardDao.userCount(search_option, keyword);
    }
	
	@Override
	public List<String> list_attach(int idx) {
		return boardDao.list_attach(idx);
	}

	@Override
	public void delete_attach(String file_name) {
		boardDao.delete_attach(file_name);

	}
	
	@Override
	public void update_attach(String file_name, int idx) {
	boardDao.update_attach(file_name, idx); }
	 
}
