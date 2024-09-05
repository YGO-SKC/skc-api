package com.rtomyj.skc.search

import com.rtomyj.skc.dao.BanListDao
import com.rtomyj.skc.dao.CardSearchDao
import com.rtomyj.skc.exception.SKCException
import com.rtomyj.skc.model.Card
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service that is used to access card info from DB.
 */
@Service
class CardSearchService @Autowired constructor(
	@Qualifier("ban-list-jdbc") val banListDao: BanListDao,
	@Qualifier("jdbc") val dao: CardSearchDao
) {

	companion object {
		private val log = LoggerFactory.getLogger(this::class.java.name)
	}


	@Throws(SKCException::class)
	fun getCardSearchResults(
		cardId: String,
		cardName: String,
		cardAttribute: String,
		cardColor: String,
		monsterType: String,
		limit: Int,
		saveBandwidth: Boolean
	): List<Card> {
		val searchResults =
			dao.searchForCardWithCriteria(cardId, cardName, cardAttribute, cardColor, monsterType, limit, false)

		if (saveBandwidth) {
			log.debug("Trimming card effects to save bandwidth.")
			Card.trimEffects(searchResults)
		}
		return searchResults
	}
}