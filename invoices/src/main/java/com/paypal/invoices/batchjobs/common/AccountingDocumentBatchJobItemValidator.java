package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.*;
import com.paypal.infrastructure.service.IgnoreProgramsService;
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

	private IgnoreProgramsService ignoreProgramsService;

	public AccountingDocumentBatchJobItemValidator(IgnoreProgramsService ignoreProgramsService) {
		this.ignoreProgramsService = ignoreProgramsService;
	}

	@Override
	public BatchJobItemValidationResult validateItem(BatchJobContext ctx,
			AbstractAccountingDocumentBatchJobItem<?> jobItem) {
		// formatter:off
		if (hasAllRequiredTokens(jobItem) && !belongsToIgnoredProgram(jobItem)) {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.VALID).build();
		}
		else if (belongsToIgnoredProgram(jobItem)) {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.INVALID)
					.reason(Optional.of(String.format(
							"Invoice documents with id [%s] should be skipped because it belongs to an ignored program",
							jobItem.getItemId())))
					.build();
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

	private boolean belongsToIgnoredProgram(BatchJobItem<? extends AccountingDocumentModel> jobItem) {
		return ignoreProgramsService.isIgnored(jobItem.getItem().getHyperwalletProgram());
	}

}
