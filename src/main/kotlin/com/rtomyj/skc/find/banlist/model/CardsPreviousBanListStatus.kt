package com.rtomyj.skc.find.banlist.model

import com.rtomyj.skc.browse.card.model.Card
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(
    implementation = CardsPreviousBanListStatus::class,
    description = "Contains info about a cards' previous ban status (forbidden, limited, semi-limited) that changed in reference to a previous logical ban list.",
)
data class CardsPreviousBanListStatus(
	@Schema(
        implementation = Card::class,
        description = "Card details",
    )
    val card: Card,

	@Schema(
        implementation = String::class,
        description = "The previous ban status the card had when compared to current ban list.",
    )
    val previousBanStatus: String
) : RepresentationModel<CardsPreviousBanListStatus?>() {
    fun setLinks() {
        card.setLinks()
    }
}