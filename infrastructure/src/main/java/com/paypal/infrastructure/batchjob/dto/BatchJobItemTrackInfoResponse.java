package com.paypal.infrastructure.batchjob.dto;

import com.paypal.infrastructure.batchjob.BatchJobItemStatus;
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
