package com.paypal.infrastructure.batchjob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data class for item id and type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchJobFailedItemId implements Serializable {

	private String id;

	private String type;

}
