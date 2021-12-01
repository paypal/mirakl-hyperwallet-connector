package com.paypal.invoices.controllers;

import com.paypal.infrastructure.controllers.AbstractJobController;
import com.paypal.invoices.dto.MiraklInvoicesMockListDTO;
import com.paypal.invoices.dto.converter.MiraklInvoiceDTOToInvoiceModelConverter;
import com.paypal.invoices.infraestructure.testing.TestingInvoicesSessionDataHelper;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.jobs.InvoicesExtractJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Specific controller to fire Extract Invoices process
 */
@Slf4j
@RestController
@RequestMapping("/job")
@Profile({ "!prod" })
public class InvoiceExtractJobMockController extends AbstractJobController {

	private static final String DEFAULT_INVOICES_EXTRACT_JOB_NAME = "InvoicesExtractJobSingleExecution";

	@Resource
	private TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelper;

	@Resource
	private MiraklInvoiceDTOToInvoiceModelConverter miraklInvoiceDTOToInvoiceModelConverter;

	/**
	 * Triggers the {@link InvoicesExtractJob} with the {@code delta} time to retrieve
	 * invoices created or updated since that {@code delta} and schedules the job with the
	 * {@code name} provided
	 * @param delta the {@link Date} in {@link DateTimeFormat.ISO}
	 * @param name the job name in {@link String}
	 * @param invoices mocked invoices received and returned by the job
	 * @return a {@link ResponseEntity <String>} with the name of the job scheduled
	 * @throws SchedulerException if quartz {@link org.quartz.Scheduler} fails
	 */
	@PostMapping("/invoices-extract")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date delta,
			@RequestParam(required = false, defaultValue = DEFAULT_INVOICES_EXTRACT_JOB_NAME) final String name,
			@RequestBody(required = false) final MiraklInvoicesMockListDTO invoices,
			@RequestParam(defaultValue = "false") final boolean enableCommissions) throws SchedulerException {
		testingInvoicesSessionDataHelper.setOperatorCommissionsEnabled(enableCommissions);
		storeInvoicesInSession(invoices);
		runSingleJob(name, InvoicesExtractJob.class, delta);

		return ResponseEntity.accepted().body(name);
	}

	private void storeInvoicesInSession(final MiraklInvoicesMockListDTO invoices) {
		//@formatter:off
		final List<InvoiceModel> convertedInvoices = Optional.ofNullable(Optional.ofNullable(invoices)
						.orElse(new MiraklInvoicesMockListDTO()).getInvoices())
				.orElse(List.of()).stream()
				.map(miraklInvoiceDTOToInvoiceModelConverter::convert)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		//@formatter:on

		testingInvoicesSessionDataHelper.setInvoices(convertedInvoices);
	}

}
