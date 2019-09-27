package com.rtomyj.yugiohAPI.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtomyj.yugiohAPI.model.BanLists;
import com.rtomyj.yugiohAPI.service.BanListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "api/v1/ban_list_dates", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin("http://localhost:3000")
public class BanListController
{
	@Autowired
	BanListService banListService;

	@GetMapping()
	public Map<String, List<BanLists>> startDatesOfBanLists() {
		List<BanLists> banStartDates = (ArrayList<BanLists>) banListService.getBanListStartDates();
		HashMap<String, List<BanLists>> response = new HashMap<>();
		response.put("banListStartDates", banStartDates);

		return response;
	}
}