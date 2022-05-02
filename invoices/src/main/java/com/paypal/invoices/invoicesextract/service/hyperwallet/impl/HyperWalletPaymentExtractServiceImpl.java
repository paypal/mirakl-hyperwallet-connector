package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.hyperwallet.clientsdk.model.HyperwalletPaymentListOptions;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Class that connects with hyperwallet to creates the payments
 */
@Slf4j
@Service
public class HyperWalletPaymentExtractServiceImpl implements HyperWalletPaymentExtractService {

	protected final Converter<InvoiceModel, HyperwalletPayment> payeeInvoiceModelToHyperwalletPaymentConverter;

	protected final Converter<InvoiceModel, HyperwalletPayment> operatorInvoiceModelToHyperwalletPaymentConverter;

	protected final Converter<CreditNoteModel, HyperwalletPayment> payeeCreditModelToHyperwalletPaymentConverter;

	protected final MailNotificationUtil mailNotificationUtil;

	protected final HyperwalletSDKService invoicesHyperwalletSDKService;

	public HyperWalletPaymentExtractServiceImpl(
			final Converter<InvoiceModel, HyperwalletPayment> payeeInvoiceModelToHyperwalletPaymentConverter,
			final Converter<InvoiceModel, HyperwalletPayment> operatorInvoiceModelToHyperwalletPaymentConverter,
			final Converter<CreditNoteModel, HyperwalletPayment> payeeCreditModelToHyperwalletPaymentConverter,
			final HyperwalletSDKService invoicesHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		this.payeeInvoiceModelToHyperwalletPaymentConverter = payeeInvoiceModelToHyperwalletPaymentConverter;
		this.operatorInvoiceModelToHyperwalletPaymentConverter = operatorInvoiceModelToHyperwalletPaymentConverter;
		this.payeeCreditModelToHyperwalletPaymentConverter = payeeCreditModelToHyperwalletPaymentConverter;
		this.mailNotificationUtil = mailNotificationUtil;
		this.invoicesHyperwalletSDKService = invoicesHyperwalletSDKService;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public void payPayeeInvoice(InvoiceModel invoice) {
		payInvoice(invoice, payeeInvoiceModelToHyperwalletPaymentConverter);
	}

	@Override
	public void payPayeeCreditNotes(final CreditNoteModel creditNote) {
		payInvoice(creditNote, payeeCreditModelToHyperwalletPaymentConverter);
	}

	@Override
	public void payInvoiceOperator(InvoiceModel invoice) {
		payInvoice(invoice, operatorInvoiceModelToHyperwalletPaymentConverter);
	}

	protected <T extends AccountingDocumentModel> void payInvoice(final T invoice,
			final Converter<T, HyperwalletPayment> invoiceConverter) {
		HyperwalletPayment pendingPayment = invoiceConverter.convert(invoice);

		if (isInvoiceCreated(pendingPayment)) {
			log.warn("Invoice {} already sent to Hyperwallet", pendingPayment.getClientPaymentId());
			return;
		}

		log.info("Pending invoices to pay: [{}]", pendingPayment.getClientPaymentId());

		HyperwalletPayment paidInvoice = createPayment(pendingPayment);

		log.info("Paid invoices: [{}]", paidInvoice.getClientPaymentId());
	}

	protected HyperwalletPayment createPayment(final HyperwalletPayment hyperwalletPayment) {
		try {
			log.info("Trying to create payment for invoice [{}]", hyperwalletPayment.getClientPaymentId());
			final Hyperwallet hyperwalletAPIClient = invoicesHyperwalletSDKService
					.getHyperwalletInstanceWithProgramToken(hyperwalletPayment.getProgramToken());
			final HyperwalletPayment payment = hyperwalletAPIClient.createPayment(hyperwalletPayment);
			log.info("Payment successfully created for invoice [{}]", hyperwalletPayment.getClientPaymentId());

			return payment;
		}
		catch (final HyperwalletException e) {
			mailNotificationUtil
					.sendPlainTextEmail("Issue detected when creating payment for an invoice in Hyperwallet",
							String.format("Something went wrong creating payment for invoice [%s]%n%s",
									hyperwalletPayment.getClientPaymentId(),
									HyperwalletLoggingErrorsUtil.stringify(e)));
			log.error("Something went wrong creating payment for invoice [{}]",
					hyperwalletPayment.getClientPaymentId());
			log.error(HyperwalletLoggingErrorsUtil.stringify(e));

			throw new HMCException("Error while invoking Hyperwallet", e);
		}
	}

	protected boolean isInvoiceCreated(HyperwalletPayment payment) {
		try {
			return getPayment(payment.getProgramToken(), payment.getClientPaymentId()).isPresent();
		}
		catch (Exception e) {
			// Let the flow of execution continue. Checking the existence of the payment
			// shouldn't abort
			// the payment creation process. If something is wrong the payment creation
			// should fail and that
			// is going to trigger the error reporting process (currently via email)s.
			return false;
		}
	}

	protected Optional<HyperwalletPayment> getPayment(final String programToken, final String clientPaymentId) {
		try {
			final Hyperwallet hyperwalletAPIClient = invoicesHyperwalletSDKService
					.getHyperwalletInstanceWithProgramToken(programToken);
			HyperwalletPaymentListOptions hyperwalletPaymentListOptions = new HyperwalletPaymentListOptions();
			hyperwalletPaymentListOptions.setClientPaymentId(clientPaymentId);
			final HyperwalletList<HyperwalletPayment> payments = hyperwalletAPIClient
					.listPayments(hyperwalletPaymentListOptions);

			return getFirstElement(payments);
		}
		catch (final HyperwalletException e) {
			log.error("Something went wrong trying to find payment for invoice [{}]", clientPaymentId);
			log.error(HyperwalletLoggingErrorsUtil.stringify(e));

			throw new HMCException("Error while invoking Hyperwallet", e);
		}
	}

	private Optional<HyperwalletPayment> getFirstElement(HyperwalletList<HyperwalletPayment> payments) {
		return Stream.ofNullable(payments).map(HyperwalletList::getData).filter(Objects::nonNull)
				.flatMap(Collection::stream).findFirst();
	}

}
