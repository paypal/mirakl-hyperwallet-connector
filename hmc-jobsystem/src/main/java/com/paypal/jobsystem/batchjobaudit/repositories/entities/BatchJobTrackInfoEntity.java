package com.paypal.jobsystem.batchjobaudit.repositories.entities;

import com.paypal.jobsystem.batchjob.model.BatchJobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
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
