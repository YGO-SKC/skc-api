package com.rtomyj.yugiohAPI.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(description = "All new cards separated by status.")
@JsonPropertyOrder({ "forbidden", "limited", "semiLimited" })
public class NewCards
{
	private List<BanListComparisonResults> forbidden;
	private List<BanListComparisonResults> limited;
	private List<BanListComparisonResults> semiLimited;
}