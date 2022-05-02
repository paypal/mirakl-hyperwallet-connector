package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletPaymentExtractServiceImplTest {

	private static final String PROGRAM_TOKEN = "programToken";

	private HyperWalletPaymentExtractServiceImpl testObj;

	@Mock
	private InvoiceModel invoiceModelOneMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKService;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletPayment paymentOneMock, createdPaymentOneMock;

	@Mock
	private HyperwalletList<HyperwalletPayment> paymentHyperwalletListMock;

	@Mock
	private Converter<InvoiceModel, HyperwalletPayment> invoiceModelToHyperwalletPaymentConverterMock;

	@Mock
	private Converter<CreditNoteModel, HyperwalletPayment> creditNoteModelHyperwalletPaymentConverterMock;

	@Mock
	private CreditNoteModel creditNoteModelOneMock;

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
	void payInvoice_shouldConvertInvoicesToHyperwalletPaymentCreatesOnePaymentPerInvoiceAndReturnsThemWhenInvoiceIsNotNull() {
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doReturn(false).when(testObj).isInvoiceCreated(paymentOneMock);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
	}

	@Test
	void payInvoice_shouldReturnNullWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doReturn(false).when(testObj).isInvoiceCreated(paymentOneMock);
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock).createPayment(paymentOneMock);

		assertThatThrownBy(() -> testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock))
				.isInstanceOf(HMCException.class);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);

	}

	@Test
	void payPayee_shouldCallPayInvoiceWithTheProperConverter() {
		doNothing().when(testObj).payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		testObj.payPayeeInvoice(invoiceModelOneMock);

		verify(testObj).payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);
	}

	@Test
	void payOperator_shouldCallPayInvoiceWithTheProperConverter() {
		doNothing().when(testObj).payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		testObj.payInvoiceOperator(invoiceModelOneMock);

		verify(testObj).payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);
	}

	@Test
	void createPayment_shouldSendAnEmailWhenAnExceptionIsThrown() {
		final HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		doThrow(hyperwalletException).when(hyperwalletMock).createPayment(paymentOneMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(paymentOneMock.getClientPaymentId()).thenReturn("000001234");
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doReturn(false).when(testObj).isInvoiceCreated(paymentOneMock);

		assertThatThrownBy(() -> testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock))
				.isInstanceOf(HMCException.class);

		verify(mailNotificationUtil).sendPlainTextEmail(
				"Issue detected when creating payment for an invoice in Hyperwallet",
				String.format("Something went wrong creating payment " + "for" + " invoice [000001234]%n%s",
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void payPayeeCreditNote_shouldConvertCreditNotesToHyperwalletPaymentCreatesOnePaymentPerCreditNoteAndReturnsThemWhenCreditNotesAreNotEmptyOrNull() {
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(paymentOneMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doReturn(false).when(testObj).isInvoiceCreated(paymentOneMock);

		testObj.payPayeeCreditNotes(creditNoteModelOneMock);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
	}

	@Test
	void payPayeeCreditNote_shouldThrowExceptionWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(paymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doReturn(false).when(testObj).isInvoiceCreated(paymentOneMock);

		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock).createPayment(paymentOneMock);

		assertThatThrownBy(() -> testObj.payPayeeCreditNotes(creditNoteModelOneMock)).isInstanceOf(HMCException.class);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
	}

	@Test
	void payInvoice_shouldNotCreateInvoice_WhenInvoiceExists() {
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);

		doReturn(true).when(testObj).isInvoiceCreated(paymentOneMock);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(testObj, times(0)).createPayment(any());
	}

	@Test
	void payInvoice_shouldCheckIfInvoiceExistsAndCreateInvoice_WhenInvoiceNotExists() {
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(hyperwalletMock.createPayment(paymentOneMock)).thenReturn(createdPaymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		when(hyperwalletMock.listPayments(any())).thenReturn(paymentHyperwalletListMock);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(hyperwalletMock).listPayments(any());
	}

	@Test
	void payInvoice_shouldCheckIfInvoiceExistsAndNotCreateInvoice_WhenInvoiceExists() {
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(hyperwalletMock.listPayments(any())).thenReturn(paymentHyperwalletListMock);
		when(paymentHyperwalletListMock.getData()).thenReturn(List.of(paymentOneMock));
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(hyperwalletMock).listPayments(any());
	}

	@Test
	void payInvoice_shouldContinueWithCreation_WhenGetPaymentFails() {
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		when(invoiceModelOneMock.getHyperwalletProgram()).thenReturn(PROGRAM_TOKEN);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		doReturn(paymentOneMock).when(testObj).createPayment(any());

		doThrow(new RuntimeException("Something went wrong")).when(hyperwalletMock).listPayments(any());

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(testObj).createPayment(any());

	}

}
