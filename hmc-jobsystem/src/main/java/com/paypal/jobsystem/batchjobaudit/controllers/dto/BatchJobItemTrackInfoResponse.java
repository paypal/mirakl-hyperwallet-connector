package com.paypal.jobsystem.batchjobaudit.controllers.dto;

import com.paypal.jobsystem.batchjob.model.BatchJobItemStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public final class BatchJobItemTrackInfoResponse {

	private final String batchJobId;

	private final String itemType;

	private final String itemId;

	private final LocalDateTime startTime;

	private final LocalDateTime finishTime;

	private final BatchJobItemStatus status;

}
