package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.infrastructure.mirakl.services.IgnoreProgramsService;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationResult;
import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationStatus;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Batch job validator for invoice items that checks if destination and program tokens are
 * fulfiled.
 */
@Component
public class AccountingDocumentBatchJobItemValidator
		implements BatchJobItemValidator<BatchJobContext, AbstractAccountingDocumentBatchJobItem<?>> {

	private final IgnoreProgramsService ignoreProgramsService;

	public AccountingDocumentBatchJobItemValidator(final IgnoreProgramsService ignoreProgramsService) {
		this.ignoreProgramsService = ignoreProgramsService;
	}

	@Override
	public BatchJobItemValidationResult validateItem(final BatchJobContext ctx,
			final AbstractAccountingDocumentBatchJobItem<?> jobItem) {
		// formatter:off
		if (hasAllRequiredTokens(jobItem) && !belongsToIgnoredProgram(jobItem)) {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.VALID).build();
		}
		else if (belongsToIgnoredProgram(jobItem)) {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.INVALID)
					.reason(Optional.of(
							"Invoice documents with id [%s] should be skipped because it belongs to an ignored program"
									.formatted(jobItem.getItemId())))
					.build();
		}
		else {
			return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.INVALID).reason(Optional
					.of("Invoice documents with id [%s] should be skipped because are lacking hw-program or bank account token"
							.formatted(jobItem.getItemId())))
					.build();
		}
		// formatter:on
	}

	private boolean hasAllRequiredTokens(final BatchJobItem<? extends AccountingDocumentModel> jobItem) {
		return StringUtils.isNotEmpty(jobItem.getItem().getHyperwalletProgram())
				&& StringUtils.isNotEmpty(jobItem.getItem().getDestinationToken());
	}

	private boolean belongsToIgnoredProgram(final BatchJobItem<? extends AccountingDocumentModel> jobItem) {
		return ignoreProgramsService.isIgnored(jobItem.getItem().getHyperwalletProgram());
	}

}
