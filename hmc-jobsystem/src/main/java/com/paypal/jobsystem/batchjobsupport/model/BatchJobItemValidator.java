package com.paypal.jobsystem.batchjobsupport.model;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationResult;

/**
 * BatchJobs will use classes implementing this interface for validating items.
 *
 * @param <C> the job context type.
 * @param <T> the item type.
 */
public interface BatchJobItemValidator<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Validates an item.
	 * @param ctx the batch job context.
	 * @param jobItem the item to be validated.
	 * @return the result of the validation.
	 */
	BatchJobItemValidationResult validateItem(C ctx, T jobItem);

}
