package com.paypal.infrastructure.batchjob;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity data for batch job failed items.
 */
@Entity
@Data
@IdClass(BatchJobFailedItemId.class)
public class BatchJobFailedItem {

	@Id
	private String id;

	@Id
	private String type;

	private LocalDateTime firstFailureTimestamp;

	private LocalDateTime lastRetryTimestamp;

	private Integer numberOfRetries;

	@NotNull
	private BatchJobFailedItemStatus status;

}
