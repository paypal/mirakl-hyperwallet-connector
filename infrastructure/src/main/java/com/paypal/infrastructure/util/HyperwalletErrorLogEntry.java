package com.paypal.infrastructure.util;

import lombok.Data;

import java.util.List;

@Data
public class HyperwalletErrorLogEntry {

	private String errorCode;

	private String errorMessage;

	private String exceptionMessage;

	private String responseStatusCode;

	private String responseMessage;

	private String responseBody;

	List<ErrorDetail> errorDetailList;

	@Data
	public static class ErrorDetail {

		private String code;

		private String fieldName;

		private String message;

		private List<String> relatedResources;

	}

}
