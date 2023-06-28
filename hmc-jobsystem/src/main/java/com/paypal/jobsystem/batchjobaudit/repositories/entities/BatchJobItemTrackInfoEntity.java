package com.paypal.jobsystem.batchjobaudit.repositories.entities;

import com.paypal.jobsystem.batchjob.model.BatchJobItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BatchJobItemTrackingInfoId.class)
public class BatchJobItemTrackInfoEntity {

	@Id
	private String batchJobId;

	@Id
	private String itemType;

	@Id
	private String itemId;

	private LocalDateTime startTime;

	private LocalDateTime finishTime;

	@NotNull
	private BatchJobItemStatus status;

}
