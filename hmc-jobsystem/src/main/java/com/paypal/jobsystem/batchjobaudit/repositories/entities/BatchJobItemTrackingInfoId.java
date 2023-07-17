package com.paypal.jobsystem.batchjobaudit.repositories.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BatchJobItemTrackingInfoId implements Serializable {

	private String batchJobId;

	private String itemType;

	private String itemId;

}
