package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.*;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Batch job validator for invoice items that checks if destination and program tokens are
 * fulfiled.
 */
@Service
public class AccountingDocumentBatchJobItemValidator
		implements BatchJobItemValidator<BatchJobContext, AbstractAccountingDocumentBatchJobItem<?>> {

	@Override
	public BatchJobItemValidationResult validateItem(BatchJobContext ctx,
			AbstractAccountingDocumentBatchJobItem<?> jobItem) {
		// formatter:off
		if (hasAllRequiredTokens(jobItem)) {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.VALID).build();
		}
		else {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.INVALID)
					.reason(Optional.of(String.format(
							"Invoice documents with id [%s] should be skipped because are lacking hw-program or bank account token",
							jobItem.getItemId())))
					.build();
		}
		// formatter:on
	}

	private boolean hasAllRequiredTokens(BatchJobItem<? extends AccountingDocumentModel> jobItem) {
		return StringUtils.isNotEmpty(jobItem.getItem().getHyperwalletProgram())
				&& StringUtils.isNotEmpty(jobItem.getItem().getDestinationToken());
	}

}
