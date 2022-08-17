package com.rtomyj.skc.find.banlist.service

import com.rtomyj.skc.util.constant.ErrConstants
import com.rtomyj.skc.find.banlist.dao.BanListDao
import com.rtomyj.skc.util.enumeration.BanListCardStatus
import com.rtomyj.skc.exception.ErrorType
import com.rtomyj.skc.exception.SKCException
import com.rtomyj.skc.browse.card.model.Card
import com.rtomyj.skc.browse.card.model.MonsterAssociation
import com.rtomyj.skc.find.banlist.model.BanListInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service class that allows interfacing with the contents of a ban list.
 */
@Service
class BannedCardsService @Autowired constructor(
	@Qualifier("ban-list-jdbc") private val banListDao: BanListDao
	, private val diffService: DiffService
) {
	companion object {
		private val log: Logger = LoggerFactory.getLogger(this::class.java)
	}


	/**
	 * Using a date, retrieves the contents of a ban list (as long as there is a ban list effective for given date).
	 * @param banListStartDate The date of the ban list to retrieve from DB. Must follow format: YYYY-DD-MM.
	 * @param saveBandwidth Restriction on what kind of ban list cards to retrieve from DB (forbidden, limited, semi-limited)
	 * @param fetchAllInfo whether all information should be fetched for a particular ban list. In this case, not only are the contents of the ban list returned
	 * , but also information on newly added cards to the ban list and cards no longer on ban list (compared to previous ban list).
	 * @return Object representation of a ban list.
	 * @throws SKCException if there is no ban list for given date.
	 */
	@Throws(SKCException::class)
	fun getBanListByDate(banListStartDate: String, saveBandwidth: Boolean, fetchAllInfo: Boolean): BanListInstance {
		log.info("Retrieving ban list w/ start date: ( {} ).", banListStartDate)

		val banListInstance: BanListInstance = BanListInstance(
			banListStartDate,
			banListDao.getPreviousBanListDate(banListStartDate),
			banListDao.getBanListByBanStatus(banListStartDate, BanListCardStatus.FORBIDDEN),
			banListDao.getBanListByBanStatus(banListStartDate, BanListCardStatus.LIMITED),
			banListDao.getBanListByBanStatus(banListStartDate, BanListCardStatus.SEMI_LIMITED)
		).apply {
			validateBanListInstance(this, banListStartDate)
			setLinks()

			if (fetchAllInfo) {
				newContent = diffService.getNewContentForGivenBanList(banListStartDate)
				removedContent = diffService.getRemovedContentForGivenBanList(banListStartDate)
			}

			if (saveBandwidth) {
				Card.trimEffects(this)
			}

			MonsterAssociation.transformMonsterLinkRating(this.forbidden)
			MonsterAssociation.transformMonsterLinkRating(this.limited)
			MonsterAssociation.transformMonsterLinkRating(this.semiLimited)
		}

		return banListInstance
	}


	@Throws(SKCException::class)
	private fun validateBanListInstance(banListInstance: BanListInstance, banListStartDate: String) {
		if (banListInstance.numForbidden == 0 && banListInstance.numLimited == 0 && banListInstance.numSemiLimited == 0) {
			throw SKCException(
				String.format(
					ErrConstants.BAN_LIST_NOT_FOUND_FOR_START_DATE, banListStartDate
				), ErrorType.DB001
			)
		}
	}
}