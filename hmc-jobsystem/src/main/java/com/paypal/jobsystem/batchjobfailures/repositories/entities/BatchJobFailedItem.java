package com.paypal.jobsystem.batchjobfailures.repositories.entities;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.NotNull;
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
