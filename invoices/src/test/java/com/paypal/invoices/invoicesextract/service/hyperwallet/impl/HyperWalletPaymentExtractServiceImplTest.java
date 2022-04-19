package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
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

import static org.assertj.core.api.Assertions.assertThat;
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

		final HyperwalletPayment result = testObj.payInvoice(invoiceModelOneMock,
				invoiceModelToHyperwalletPaymentConverterMock);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);

		assertThat(result).isEqualTo(createdPaymentOneMock);
	}

	@Test
	void payInvoice_shouldReturnNullWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock).createPayment(paymentOneMock);

		assertThatThrownBy(() -> testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock))
				.isInstanceOf(HMCException.class);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);

	}

	@Test
	void payPayee_shouldCallPayInvoiceWithTheProperConverter() {
		doReturn(paymentOneMock).when(testObj).payInvoice(invoiceModelOneMock,
				invoiceModelToHyperwalletPaymentConverterMock);

		final HyperwalletPayment result = testObj.payPayeeInvoice(invoiceModelOneMock);

		verify(testObj).payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);
		assertThat(result).isEqualTo(paymentOneMock);
	}

	@Test
	void payOperator_shouldCallPayInvoiceWithTheProperConverter() {
		doReturn(createdPaymentOneMock).when(testObj).payInvoice(invoiceModelOneMock,
				invoiceModelToHyperwalletPaymentConverterMock);

		final HyperwalletPayment result = testObj.payInvoiceOperator(invoiceModelOneMock);

		verify(testObj).payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);
		assertThat(result).isEqualTo(createdPaymentOneMock);
	}

	@Test
	void createPayment_shouldSendAnEmailWhenAnExceptionIsThrown() {
		final HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		doThrow(hyperwalletException).when(hyperwalletMock).createPayment(paymentOneMock);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		when(paymentOneMock.getClientPaymentId()).thenReturn("000001234");
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);

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

		final HyperwalletPayment result = testObj.payPayeeCreditNotes(creditNoteModelOneMock);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);

		assertThat(result).isEqualTo(createdPaymentOneMock);
	}

	@Test
	void payPayeeCreditNote_shouldThrowExceptionWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(paymentOneMock);
		when(paymentOneMock.getProgramToken()).thenReturn(PROGRAM_TOKEN);
		when(hyperwalletSDKService.getHyperwalletInstanceWithProgramToken(PROGRAM_TOKEN)).thenReturn(hyperwalletMock);
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock).createPayment(paymentOneMock);

		assertThatThrownBy(() -> testObj.payPayeeCreditNotes(creditNoteModelOneMock)).isInstanceOf(HMCException.class);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(hyperwalletMock).createPayment(paymentOneMock);
	}

}
