package com.paypal.infrastructure.batchjob.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchJobFailedItemResponse {

	private String id;

	private String type;

	private LocalDateTime firstFailureTimestamp;

	private LocalDateTime lastRetryTimestamp;

	private Integer numberOfRetries;

	private String status;

}
