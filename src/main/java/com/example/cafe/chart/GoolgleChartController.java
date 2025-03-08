package com.example.cafe.chart;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/chart/*")
public class GoolgleChartController {
	
	@Autowired
	GoolgleChartService goolgleChartService;
	
	@GetMapping("chart1.do")
	public ModelAndView chart1() {
		return new ModelAndView("chart/chart01");
	}
	
	@GetMapping("chart2.do")
	public ModelAndView chart2() {
		return new ModelAndView("chart/chart02");
	}
	
	@GetMapping("cart_money_list.do")
	public JSONObject cart_money_list() {
		return goolgleChartService.getChartData();
	}
}
