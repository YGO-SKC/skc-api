package com.rtomyj.yugiohAPI.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YgoException extends Exception
{
	private static final long serialVersionUID = 1L;

	private  String code;
	private String message;
}