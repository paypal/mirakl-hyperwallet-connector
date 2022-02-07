package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
	 */
	@Override
	public List<HyperwalletPayment> payPayeeInvoice(final List<InvoiceModel> invoices) {
		return payInvoice(invoices, payeeInvoiceModelToHyperwalletPaymentConverter);
	}

	@Override
	public List<HyperwalletPayment> payPayeeCreditNote(final List<CreditNoteModel> creditNotes) {
		final List<HyperwalletPayment> pendingCreditNotes = creditNotes.stream()
				.map(payeeCreditModelToHyperwalletPaymentConverter::convert).filter(Objects::nonNull)
				.collect(Collectors.toList());

		log.info("Pending credit notes to pay: [{}]", creditNotes.size());

		final List<HyperwalletPayment> paidCreditNotes = pendingCreditNotes.stream().map(this::createPayment)
				.filter(Objects::nonNull).collect(Collectors.toList());

		log.info("Paid creditNotes: [{}]",
				paidCreditNotes.stream().map(HyperwalletPayment::getClientPaymentId).collect(Collectors.joining(",")));

		return paidCreditNotes;
	}

	@Override
	public List<HyperwalletPayment> payInvoiceOperator(final List<InvoiceModel> invoices) {
		return payInvoice(invoices, operatorInvoiceModelToHyperwalletPaymentConverter);
	}

	protected List<HyperwalletPayment> payInvoice(final List<InvoiceModel> invoices,
			final Converter<InvoiceModel, HyperwalletPayment> invoiceConverter) {
		//@formatter:off
		final List<HyperwalletPayment> pendingPayments = invoices.stream()
				.map(invoiceConverter::convert)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		log.info("Pending invoices to pay: [{}]", invoices.size());

		final List<HyperwalletPayment> paidInvoices = pendingPayments.stream()
				.map(this::createPayment)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		log.info("Paid invoices: [{}]",
				paidInvoices.stream()
						.map(HyperwalletPayment::getClientPaymentId)
						.collect(Collectors.joining(",")));

		return paidInvoices;
		//@formatter:on
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

			return null;
		}
	}

}
