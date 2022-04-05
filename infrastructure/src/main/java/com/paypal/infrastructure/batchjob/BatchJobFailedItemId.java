package com.paypal.infrastructure.batchjob;

import lombok.Data;

import java.io.Serializable;

/**
 * Data class for item id and type.
 */
@Data
public class BatchJobFailedItemId implements Serializable {

	private final String id;

	private final String type;

}
