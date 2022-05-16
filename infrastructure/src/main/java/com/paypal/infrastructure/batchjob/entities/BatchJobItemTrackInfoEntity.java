package com.paypal.infrastructure.batchjob.entities;

import com.paypal.infrastructure.batchjob.BatchJobItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.NotNull;
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
