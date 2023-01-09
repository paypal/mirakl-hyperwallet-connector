package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.invoices.InvoicesIntegrationTests;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

class HyperWalletPaymentExtractServiceImplTest extends InvoicesIntegrationTests {

	private static final String DEFAULT_INVOICE_NUMBER = "invoiceNumber";

	private static final String DEFAULT_HYPERWALLET_PROGRAM = "DEFAULT";

	private static final String DEFAULT_DESTINATION_TOKEN = "testDestinationToken";

	private static final double DEFAULT_TRANSFER_AMOUNT = 11.1D;

	private static final String GB_ISOCODE = "GB";

	@Autowired
	private HyperWalletPaymentExtractServiceImpl testObj;

	@SpyBean
	private MailNotificationUtil mailNotificationUtil;

	@Test
	void payInvoice_shouldCreateAPayment_WhenNotExistAnotherCorrectPayment() {
		final HyperwalletPayment expected = defaultHyperwalletPayment();
		final InvoiceModel invoice = defaultInvoice();

		mockToCreatePayment(expected);

		Optional<HyperwalletPayment> result = testObj.payPayeeInvoice(invoice);

		Assertions.assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void payInvoice_shouldSendAnEmail_WhenAnExceptionIsThrownInCreationPaymentRequest() {
		final HyperwalletPayment payment = defaultHyperwalletPayment().clientPaymentId("000001234");
		final InvoiceModel invoice = defaultInvoice().toBuilder().invoiceNumber("000001234").build();

		paymentsEndpointMock.listPaymentsRequest(invoice.getInvoiceNumber(), Collections.emptyList());
		paymentsEndpointMock.createPaymentErrorRequest(payment);

		assertThatThrownBy(() -> testObj.payPayeeInvoice(invoice)).isInstanceOf(HMCException.class);
		verify(mailNotificationUtil).sendPlainTextEmail(
				ArgumentMatchers.eq("Issue detected when creating payment for an invoice in Hyperwallet"),
				ArgumentMatchers.contains("Something went wrong creating payment for invoice [000001234]"));
	}

	@Test
	void payInvoice_shouldContinueWithCreationPayment_WhenGetPaymentFails() {
		final HyperwalletPayment expected = defaultHyperwalletPayment();
		final InvoiceModel invoice = defaultInvoice();

		paymentsEndpointMock.listPaymentsErrorRequest(invoice.getInvoiceNumber());
		paymentsEndpointMock.createPaymentRequest(expected);

		Optional<HyperwalletPayment> result = testObj.payPayeeInvoice(invoice);

		Assertions.assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);

	}

	@Test
	void payInvoice_willStopCreation_WhenSomePaymentHasAGoodStatus() {

		final InvoiceModel invoice = defaultInvoice();

		paymentsEndpointMock.listPaymentsRequest(invoice.getInvoiceNumber(),
				Arrays.asList("FAILED", "NO_FAILURE_STATUS", "RETURNED"));

		Optional<HyperwalletPayment> result = testObj.payPayeeInvoice(invoice);

		assertThat(result).isEmpty();
	}

	@Test
	void payPayeeCreditNote_shouldCreateAPayment_WhenNotExistAnotherCorrectPayment() {
		final HyperwalletPayment expected = defaultHyperwalletPayment();
		final CreditNoteModel creditNote = defaultCreditModel();

		mockToCreatePayment(expected);
		Optional<HyperwalletPayment> result = testObj.payPayeeCreditNotes(creditNote);

		Assertions.assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void payPayeeCreditNote_shouldThrowExceptionWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		final CreditNoteModel creditNote = defaultCreditModel();

		paymentsEndpointMock.listPaymentsRequest(creditNote.getInvoiceNumber(), Collections.emptyList());
		paymentsEndpointMock.createPaymentErrorRequest(payment);

		assertThatThrownBy(() -> testObj.payPayeeCreditNotes(creditNote)).isInstanceOf(HMCException.class);
	}

	@Test
	void payInvoiceOperator_shouldCreateAPayment_WhenNotExistAnotherCorrectPayment() {
		final HyperwalletPayment expected = defaultHyperwalletPayment().clientPaymentId("invoiceNumber-operatorFee")
				.destinationToken("test2");
		final InvoiceModel invoice = defaultInvoiceOperator();

		mockToCreatePayment(expected);
		Optional<HyperwalletPayment> result = testObj.payInvoiceOperator(invoice);

		Assertions.assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
	}

	private HyperwalletPayment defaultHyperwalletPayment() {
		return new HyperwalletPayment().programToken("test").clientPaymentId(DEFAULT_INVOICE_NUMBER)
				.destinationToken(DEFAULT_DESTINATION_TOKEN).amount(DEFAULT_TRANSFER_AMOUNT).currency(GB_ISOCODE)
				.purpose("OTHER");
	}

	private InvoiceModel defaultInvoice() {
		return InvoiceModel.builder().hyperwalletProgram(DEFAULT_HYPERWALLET_PROGRAM)
				.destinationToken(DEFAULT_DESTINATION_TOKEN).invoiceNumber(DEFAULT_INVOICE_NUMBER)
				.transferAmount(DEFAULT_TRANSFER_AMOUNT).currencyIsoCode(GB_ISOCODE).build();
	}

	private InvoiceModel defaultInvoiceOperator() {
		return InvoiceModel.builder().hyperwalletProgram(DEFAULT_HYPERWALLET_PROGRAM)
				.destinationToken(DEFAULT_DESTINATION_TOKEN).invoiceNumber(DEFAULT_INVOICE_NUMBER)
				.transferAmountToOperator(DEFAULT_TRANSFER_AMOUNT).currencyIsoCode(GB_ISOCODE).build();
	}

	private CreditNoteModel defaultCreditModel() {
		return CreditNoteModel.builder().hyperwalletProgram(DEFAULT_HYPERWALLET_PROGRAM)
				.destinationToken(DEFAULT_DESTINATION_TOKEN).invoiceNumber(DEFAULT_INVOICE_NUMBER)
				.creditAmount(DEFAULT_TRANSFER_AMOUNT).currencyIsoCode(GB_ISOCODE).build();
	}

	private void mockToCreatePayment(HyperwalletPayment payment) {
		final Collection<String> statuses = Arrays.asList("FAILED", "RECALLED", "RETURNED");

		paymentsEndpointMock.listPaymentsRequest(payment.getClientPaymentId(), statuses);

		paymentsEndpointMock.createPaymentRequest(payment);
	}

}
