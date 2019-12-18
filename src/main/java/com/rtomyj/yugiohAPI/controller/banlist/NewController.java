package com.rtomyj.yugiohAPI.controller.banlist;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rtomyj.yugiohAPI.dao.database.Dao.Status;
import com.rtomyj.yugiohAPI.helper.LogHelper;
import com.rtomyj.yugiohAPI.service.banlist.DiffService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${ygo.endpoints.v1.ban-list-new-cards}", produces = "application/json; charset=UTF-8")
@CrossOrigin(origins = "*")
public class NewController
{
	@Autowired
	private DiffService banListDiffService;

	@Value("${ygo.endpoints.v1.ban-list-new-cards}")
	private String endPoint;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	@Qualifier("banListNewCardsCache")
	private Map<String, Map<String, Object>> cache;

	private static final Logger LOG = LogManager.getLogger();



	@GetMapping(path = "/{banListDate}")
	public ResponseEntity<Map<String, Object>> getNewContent(@PathVariable final String banListDate)
	{
		HttpStatus requestStatus = null;
		Map<String, Object> newCardsMeta = cache.get(banListDate);
		boolean inCache = false;

		if (newCardsMeta == null)
		{
			final Map<String, List<Map<String, String>>> newCards = new LinkedHashMap<>();
			newCards.put("forbidden", banListDiffService.getNewContentOfBanList(banListDate, Status.FORBIDDEN.toString()));
			newCards.put("limited", banListDiffService.getNewContentOfBanList(banListDate, Status.LIMITED.toString()));
			newCards.put("semiLimited", banListDiffService.getNewContentOfBanList(banListDate, Status.SEMI_LIMITED.toString()));

			newCardsMeta = new HashMap<>();
			newCardsMeta.put("listRequested", banListDate);
			newCardsMeta.put("comparedTo", banListDiffService.getPreviousBanListDate(banListDate));
			newCardsMeta.put("newCards", newCards);

			cache.put(banListDate, newCardsMeta);
			requestStatus = HttpStatus.OK;
		}
		else
		{
			inCache = true;
			requestStatus = HttpStatus.OK;
		}

		LOG.info(LogHelper.requestStatusLogString(request.getRemoteHost(), banListDate, endPoint, requestStatus, inCache));
		return new ResponseEntity<>(newCardsMeta, requestStatus);
	}
}