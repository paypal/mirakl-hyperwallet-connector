package com.paypal.infrastructure.batchjob.entities;

import com.paypal.infrastructure.batchjob.BatchJobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BatchJobTrackInfoEntity {

	@Id
	private String batchJobId;

	@NotNull
	private String batchJobType;

	@NotNull
	private LocalDateTime startTime;

	private LocalDateTime finishTime;

	@NotNull
	private BatchJobStatus status;

	@OneToMany
	@JoinColumn(name = "batchJobId")
	private List<BatchJobItemTrackInfoEntity> items;

}
