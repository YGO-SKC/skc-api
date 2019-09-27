package com.rtomyj.yugiohAPI.dao;

import com.rtomyj.yugiohAPI.model.BanLists;
import com.rtomyj.yugiohAPI.model.Card;

import java.util.List;

public interface YGO_API_dao
{
	public List<BanLists> getBanListStartDates();
	public Card getCardInfo(String cardID);
	public List<Card> getBanListByBanStatus(String date, String status);
}