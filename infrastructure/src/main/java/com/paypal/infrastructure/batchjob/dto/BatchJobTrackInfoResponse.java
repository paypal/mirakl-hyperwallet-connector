package com.paypal.infrastructure.batchjob.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchJobTrackInfoResponse {

	private String batchJobId;

	private String batchJobType;

	private LocalDateTime startTime;

	private LocalDateTime finishTime;

	private String status;

}
