package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
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
	 * @return
	 */
	@Override
	public HyperwalletPayment payPayeeInvoice(InvoiceModel invoice) {
		return payInvoice(invoice, payeeInvoiceModelToHyperwalletPaymentConverter);
	}

	@Override
	public HyperwalletPayment payPayeeCreditNotes(final CreditNoteModel creditNote) {
		return payInvoice(creditNote, payeeCreditModelToHyperwalletPaymentConverter);
	}

	@Override
	public HyperwalletPayment payInvoiceOperator(InvoiceModel invoice) {
		return payInvoice(invoice, operatorInvoiceModelToHyperwalletPaymentConverter);
	}

	protected <T extends AccountingDocumentModel> HyperwalletPayment payInvoice(final T invoice,
			final Converter<T, HyperwalletPayment> invoiceConverter) {
		HyperwalletPayment pendingPayment = invoiceConverter.convert(invoice);

		log.info("Pending invoices to pay: [{}]", invoice.getInvoiceNumber());

		HyperwalletPayment paidInvoice = createPayment(pendingPayment);

		if (paidInvoice != null) {
			log.info("Paid invoices: [{}]", paidInvoice.getClientPaymentId());
		}

		return paidInvoice;
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

}
