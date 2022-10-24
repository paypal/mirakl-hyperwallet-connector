package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
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

	protected final PaymentHyperwalletApiClient client;

	protected final PaymentNotificationConfig paymentNotificationConfig;

	public HyperWalletPaymentExtractServiceImpl(
			final Converter<InvoiceModel, HyperwalletPayment> payeeInvoiceModelToHyperwalletPaymentConverter,
			final Converter<InvoiceModel, HyperwalletPayment> operatorInvoiceModelToHyperwalletPaymentConverter,
			final Converter<CreditNoteModel, HyperwalletPayment> payeeCreditModelToHyperwalletPaymentConverter,
			final MailNotificationUtil mailNotificationUtil, final PaymentHyperwalletApiClient client,
			final PaymentNotificationConfig paymentNotificationConfig) {
		this.payeeInvoiceModelToHyperwalletPaymentConverter = payeeInvoiceModelToHyperwalletPaymentConverter;
		this.operatorInvoiceModelToHyperwalletPaymentConverter = operatorInvoiceModelToHyperwalletPaymentConverter;
		this.payeeCreditModelToHyperwalletPaymentConverter = payeeCreditModelToHyperwalletPaymentConverter;
		this.mailNotificationUtil = mailNotificationUtil;
		this.client = client;
		this.paymentNotificationConfig = paymentNotificationConfig;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public void payPayeeInvoice(final InvoiceModel invoice) {
		payInvoice(invoice, payeeInvoiceModelToHyperwalletPaymentConverter);
	}

	@Override
	public void payPayeeCreditNotes(final CreditNoteModel creditNote) {
		payInvoice(creditNote, payeeCreditModelToHyperwalletPaymentConverter);
	}

	@Override
	public void payInvoiceOperator(final InvoiceModel invoice) {
		payInvoice(invoice, operatorInvoiceModelToHyperwalletPaymentConverter);
	}

	protected <T extends AccountingDocumentModel> void payInvoice(final T invoice,
			final Converter<T, HyperwalletPayment> invoiceConverter) {
		final HyperwalletPayment pendingPayment = invoiceConverter.convert(invoice);

		if (isInvoiceCreated(pendingPayment)) {
			log.warn("Invoice {} already sent to Hyperwallet", pendingPayment.getClientPaymentId());
			return;
		}

		log.info("Pending invoices to pay: [{}]", pendingPayment.getClientPaymentId());

		final HyperwalletPayment paidInvoice = createPayment(pendingPayment);

		log.info("Paid invoices: [{}]", paidInvoice.getClientPaymentId());
	}

	protected HyperwalletPayment createPayment(final HyperwalletPayment hyperwalletPayment) {
		try {
			log.info("Trying to create payment for invoice [{}]", hyperwalletPayment.getClientPaymentId());
			final HyperwalletPayment payment = client.createPayment(hyperwalletPayment);
			log.info("Payment successfully created for invoice [{}]", hyperwalletPayment.getClientPaymentId());

			return payment;
		}
		catch (final HyperwalletException e) {
			mailNotificationUtil
					.sendPlainTextEmail("Issue detected when creating payment for an invoice in Hyperwallet",
							String.format("Something went wrong creating payment for invoice [%s]%n%s",
									hyperwalletPayment.getClientPaymentId(),
									HyperwalletLoggingErrorsUtil.stringify(e)));
			log.error(String.format("Something went wrong creating payment for invoice [%s].%n%s",
					hyperwalletPayment.getClientPaymentId(), HyperwalletLoggingErrorsUtil.stringify(e)), e);

			throw new HMCException("Error while invoking Hyperwallet", e);
		}
	}

	protected boolean isInvoiceCreated(final HyperwalletPayment payment) {
		try {
			return getPayments(payment.getProgramToken(), payment.getClientPaymentId()).stream()
					.map(HyperwalletPayment::getStatus)
					.anyMatch(status -> !paymentNotificationConfig.getFailureStatuses().contains(status));
		}
		catch (final Exception e) {
			// Let the flow of execution continue. Checking the existence of the payment
			// shouldn't abort
			// the payment creation process. If something is wrong the payment creation
			// should fail and that
			// is going to trigger the error reporting process (currently via email)s.
			return false;
		}
	}

	protected Collection<HyperwalletPayment> getPayments(final String programToken, final String clientPaymentId) {
		try {
			final HyperwalletList<HyperwalletPayment> payments = client.listPayments(programToken, clientPaymentId);

			return extractPaymentsFromQueryDTO(payments);
		}
		catch (final HyperwalletException e) {
			log.error(String.format("Something went wrong trying to find payment for invoice [%s].%n%s",
					clientPaymentId, HyperwalletLoggingErrorsUtil.stringify(e)), e);

			throw new HMCException("Error while invoking Hyperwallet", e);
		}
	}

	private Collection<HyperwalletPayment> extractPaymentsFromQueryDTO(
			final HyperwalletList<HyperwalletPayment> payments) {
		return Stream.ofNullable(payments).map(HyperwalletList::getData).filter(Objects::nonNull)
				.flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
	}

}
