package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletPaymentExtractServiceImplTest {

	private static final String PROGRAM_TOKEN = "programToken";

	private HyperWalletPaymentExtractServiceImpl testObj;

	@Mock
	private InvoiceModel invoiceModelOneMock, invoiceModelTwoMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKService;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletPayment paymentOneMock, paymentTwoMock, createdPaymentOneMock, createdPaymentTwoMock;

	@Mock
	private Converter<InvoiceModel, HyperwalletPayment> invoiceModelToHyperwalletPaymentConverterMock;

	@Mock
	private Converter<CreditNoteModel, HyperwalletPayment> creditNoteModelHyperwalletPaymentConverterMock;

	@Mock
	private CreditNoteModel creditNoteModelOneMock, creditNoteModelTwoMock;

	@Mock
	private MailNotificationUtil mailNotificationUtil;

	@BeforeEach
	void setUp() {
		testObj = new HyperWalletPaymentExtractServiceImpl(invoiceModelToHyperwalletPaymentConverterMock,
				invoiceModelToHyperwalletPaymentConverterMock, creditNoteModelHyperwalletPaymentConverterMock,
				hyperwalletSDKService, mailNotificationUtil);
		testObj = Mockito.spy(testObj);
	}

	@Test
	void payInvoice_shouldConvertInvoicesToHyperwalletPaymentCreatesOnePaymentPerInvoiceAndReturnsThemWhenInvoicesIsNotEmptyOrNull() {
		final List<InvoiceModel> invoices = List.of(invoiceModelOneMock, invoiceModelTwoMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelTwoMock)).thenReturn(paymentTwoMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(hyperwalletMock.createPayment(paymentTwoMock)).thenReturn(createdPaymentTwoMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(paymentTwoMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

		final List<HyperwalletPayment> result = testObj.payInvoice(invoices,
				invoiceModelToHyperwalletPaymentConverterMock);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelTwoMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
		verify(hyperwalletMock).createPayment(paymentTwoMock);

		assertThat(result).containsExactlyInAnyOrder(createdPaymentOneMock, createdPaymentTwoMock);
	}

	@Test
	void payInvoice_shouldReturnOnlyPaymentsSuccessfullyCreatedOnHyperwallet() {
		final List<InvoiceModel> invoices = List.of(invoiceModelOneMock, invoiceModelTwoMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelTwoMock)).thenReturn(paymentTwoMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(paymentTwoMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock).createPayment(paymentTwoMock);

		final List<HyperwalletPayment> result = testObj.payInvoice(invoices,
				invoiceModelToHyperwalletPaymentConverterMock);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelTwoMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
		verify(hyperwalletMock).createPayment(paymentTwoMock);

		assertThat(result).containsExactlyInAnyOrder(createdPaymentOneMock);
	}

	@Test
	void payInvoice_shouldOnlyProcessPaymentsNonNullHyperwalletPaymentObjects() {
		final List<InvoiceModel> invoices = List.of(invoiceModelOneMock, invoiceModelTwoMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelTwoMock)).thenReturn(null);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

		final List<HyperwalletPayment> result = testObj.payInvoice(invoices,
				invoiceModelToHyperwalletPaymentConverterMock);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelTwoMock);

		verify(hyperwalletMock).createPayment(paymentOneMock);

		assertThat(result).containsExactlyInAnyOrder(createdPaymentOneMock);
	}

	@Test
	void payInvoice_shouldReturnAnEmptyListWhenThereIsNoInvoicesToProcess() {
		final List<InvoiceModel> invoices = Collections.emptyList();

		final List<HyperwalletPayment> result = testObj.payInvoice(invoices,
				invoiceModelToHyperwalletPaymentConverterMock);

		assertThat(result).isEmpty();
	}

	@Test
	void payPayee_shouldCallPayInvoiceWithTheProperConverter() {
		final List<InvoiceModel> invoices = List.of(invoiceModelOneMock, invoiceModelTwoMock);
		final var createdPayments = List.of(this.paymentOneMock, paymentTwoMock);
		doReturn(createdPayments).when(testObj).payInvoice(invoices, invoiceModelToHyperwalletPaymentConverterMock);

		final var result = testObj.payPayeeInvoice(invoices);

		verify(testObj).payInvoice(invoices, invoiceModelToHyperwalletPaymentConverterMock);
		assertThat(result).isEqualTo(createdPayments);
	}

	@Test
	void payOperator_shouldCallPayInvoiceWithTheProperConverter() {
		final List<InvoiceModel> invoices = List.of(invoiceModelOneMock, invoiceModelTwoMock);
		final var createdPayments = List.of(this.paymentOneMock, paymentTwoMock);
		doReturn(createdPayments).when(testObj).payInvoice(invoices, invoiceModelToHyperwalletPaymentConverterMock);

		final var result = testObj.payInvoiceOperator(invoices);

		verify(testObj).payInvoice(invoices, invoiceModelToHyperwalletPaymentConverterMock);
		assertThat(result).isEqualTo(createdPayments);
	}

	@Test
	void createPayment_shouldSendAnEmailWhenAnExceptionIsThrown() {
		final var hyperwalletException = new HyperwalletException("Something went wrong");
		doThrow(hyperwalletException).when(hyperwalletMock).createPayment(paymentOneMock);
		when(paymentOneMock.getClientPaymentId()).thenReturn("000001234");
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

		testObj.createPayment(paymentOneMock);

		verify(mailNotificationUtil).sendPlainTextEmail(
				"Issue detected when creating payment for an invoice in Hyperwallet",
				String.format("Something went wrong creating payment " + "for" + " invoice [000001234]%n%s",
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void payPayeeCreditNote_shouldConvertCreditNotesToHyperwalletPaymentCreatesOnePaymentPerCreditNoteAndReturnsThemWhenCreditNotesAreNotEmptyOrNull() {
		final List<CreditNoteModel> creditNoteList = List.of(creditNoteModelOneMock, creditNoteModelTwoMock);
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(paymentOneMock);
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelTwoMock)).thenReturn(paymentTwoMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(hyperwalletMock.createPayment(paymentTwoMock)).thenReturn(createdPaymentTwoMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(paymentTwoMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);

		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

		final List<HyperwalletPayment> result = testObj.payPayeeCreditNote(creditNoteList);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelTwoMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
		verify(hyperwalletMock).createPayment(paymentTwoMock);

		assertThat(result).containsExactlyInAnyOrder(createdPaymentOneMock, createdPaymentTwoMock);
	}

	@Test
	void payPayeeCreditNote_shouldReturnOnlyPaymentsSuccessfullyCreatedOnHyperwallet() {
		final List<CreditNoteModel> creditNoteList = List.of(creditNoteModelOneMock, creditNoteModelTwoMock);
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(paymentOneMock);
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelTwoMock)).thenReturn(paymentTwoMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(paymentTwoMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock).createPayment(paymentTwoMock);

		final List<HyperwalletPayment> result = testObj.payPayeeCreditNote(creditNoteList);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelTwoMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
		verify(hyperwalletMock).createPayment(paymentTwoMock);

		assertThat(result).containsExactlyInAnyOrder(createdPaymentOneMock);
	}

	@Test
	void payPayeeCreditNote_shouldOnlyProcessPaymentsNonNullHyperwalletPaymentObjects() {
		final List<CreditNoteModel> creditNoteList = List.of(creditNoteModelOneMock, creditNoteModelTwoMock);
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(paymentOneMock);
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelTwoMock)).thenReturn(null);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

		final List<HyperwalletPayment> result = testObj.payPayeeCreditNote(creditNoteList);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelTwoMock);

		verify(hyperwalletMock).createPayment(paymentOneMock);

		assertThat(result).containsExactlyInAnyOrder(createdPaymentOneMock);
	}

	@Test
	void payCreditNote_shouldReturnAnEmptyListWhenThereIsNoInvoicesToProcess() {
		final List<CreditNoteModel> creditNoteList = Collections.emptyList();

		final List<HyperwalletPayment> result = testObj.payPayeeCreditNote(creditNoteList);

		assertThat(result).isEmpty();
	}

}
