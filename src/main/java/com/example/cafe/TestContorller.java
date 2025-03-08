package com.example.cafe;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cafe.search.model.CafeDAO;

import jakarta.servlet.http.HttpSession;

@RestController
public class TestContorller {
	@Autowired
	CafeDAO cafeDao;
	
    @GetMapping("/checkSession.do")
    public Map<String, Object> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        response.put("cf_number", session.getAttribute("cf_number"));
        response.put("userid", session.getAttribute("userid"));
        response.put("user_role", session.getAttribute("user_role"));
        response.put("user_name", session.getAttribute("user_name"));
        response.put("user_au_lv", session.getAttribute("user_au_lv"));
        return response;
    }
	
}
