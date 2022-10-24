package com.paypal.invoices.invoicesextract.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletPaymentExtractServiceImplTest {

	private static final String PROGRAM_TOKEN = "programToken";

	public static final String PAYMENT_ID = "paymentId";

	private HyperWalletPaymentExtractServiceImpl testObj;

	@Mock
	private InvoiceModel invoiceModelOneMock;

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

	@Mock
	private PaymentHyperwalletApiClient paymentHyperwalletApiClient;

	@Mock
	private PaymentNotificationConfig paymentNotificationConfig;

	@BeforeEach
	void setUp() {
		testObj = new HyperWalletPaymentExtractServiceImpl(invoiceModelToHyperwalletPaymentConverterMock,
				invoiceModelToHyperwalletPaymentConverterMock, creditNoteModelHyperwalletPaymentConverterMock,
				mailNotificationUtil, paymentHyperwalletApiClient, paymentNotificationConfig);
		testObj = Mockito.spy(testObj);
	}

	@Test
	void payInvoice_shouldConvertInvoicesToHyperwalletPaymentCreatesOnePaymentPerInvoiceAndReturnsThemWhenInvoiceIsNotNull() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(payment);
		when(paymentHyperwalletApiClient.createPayment(payment)).thenReturn(createdPaymentOneMock);
		doReturn(false).when(testObj).isInvoiceCreated(payment);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
	}

	@Test
	void payInvoice_shouldReturnNullWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(payment);
		when(paymentHyperwalletApiClient.createPayment(payment))
				.thenThrow(new HyperwalletException("Something went wrong"));
		doReturn(false).when(testObj).isInvoiceCreated(payment);

		assertThatThrownBy(() -> testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock))
				.isInstanceOf(HMCException.class);

		verify(invoiceModelToHyperwalletPaymentConverterMock).convert(invoiceModelOneMock);
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
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(payment);
		when(paymentHyperwalletApiClient.createPayment(payment)).thenThrow(hyperwalletException);
		doReturn(false).when(testObj).isInvoiceCreated(payment);
		payment.setClientPaymentId("000001234");
		assertThatThrownBy(() -> testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock))
				.isInstanceOf(HMCException.class);

		verify(mailNotificationUtil).sendPlainTextEmail(
				"Issue detected when creating payment for an invoice in Hyperwallet",
				String.format("Something went wrong creating payment " + "for" + " invoice [000001234]%n%s",
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void payPayeeCreditNote_shouldConvertCreditNotesToHyperwalletPaymentCreatesOnePaymentPerCreditNoteAndReturnsThemWhenCreditNotesAreNotEmptyOrNull() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(payment);
		when(paymentHyperwalletApiClient.createPayment(payment)).thenReturn(createdPaymentOneMock);
		doReturn(false).when(testObj).isInvoiceCreated(payment);

		testObj.payPayeeCreditNotes(creditNoteModelOneMock);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(paymentHyperwalletApiClient).createPayment(payment);
	}

	@Test
	void payPayeeCreditNote_shouldThrowExceptionWhenPaymentNotSuccessfullyCreatedOnHyperwallet() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(creditNoteModelHyperwalletPaymentConverterMock.convert(creditNoteModelOneMock)).thenReturn(payment);
		doReturn(false).when(testObj).isInvoiceCreated(payment);

		when(paymentHyperwalletApiClient.createPayment(payment))
				.thenThrow(new HyperwalletException("Something went wrong"));

		assertThatThrownBy(() -> testObj.payPayeeCreditNotes(creditNoteModelOneMock)).isInstanceOf(HMCException.class);

		verify(creditNoteModelHyperwalletPaymentConverterMock).convert(creditNoteModelOneMock);
		verify(paymentHyperwalletApiClient).createPayment(payment);
	}

	@Test
	void payInvoice_shouldNotCreateInvoice_WhenInvoiceExists() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(payment);

		doReturn(true).when(testObj).isInvoiceCreated(payment);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(testObj, times(0)).createPayment(any());
	}

	@Test
	void payInvoice_shouldCheckIfInvoiceExistsAndCreateInvoice_WhenInvoiceNotExists() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(payment);
		when(paymentHyperwalletApiClient.createPayment(payment)).thenReturn(createdPaymentOneMock);
		when(paymentHyperwalletApiClient.listPayments(anyString(), anyString())).thenReturn(paymentHyperwalletListMock);

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(paymentHyperwalletApiClient).listPayments(PROGRAM_TOKEN, PAYMENT_ID);
	}

	@Test
	void payInvoice_shouldCheckIfInvoiceExistsAndNotCreateInvoice_WhenInvoiceExists() {
		final HyperwalletPayment payment = defaultHyperwalletPayment();
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(payment);
		when(paymentHyperwalletApiClient.listPayments(PROGRAM_TOKEN, PAYMENT_ID))
				.thenReturn(paymentHyperwalletListMock);
		when(paymentHyperwalletListMock.getData()).thenReturn(List.of(payment));

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(paymentHyperwalletApiClient).listPayments(PROGRAM_TOKEN, PAYMENT_ID);
	}

	@Test
	void payInvoice_shouldContinueWithCreation_WhenGetPaymentFails() {
		when(invoiceModelOneMock.getHyperwalletProgram()).thenReturn(PROGRAM_TOKEN);
		when(invoiceModelToHyperwalletPaymentConverterMock.convert(invoiceModelOneMock)).thenReturn(paymentOneMock);
		doReturn(paymentOneMock).when(testObj).createPayment(any());

		when(paymentHyperwalletApiClient.listPayments(anyString(), anyString()))
				.thenThrow(new RuntimeException("Something went wrong"));

		testObj.payInvoice(invoiceModelOneMock, invoiceModelToHyperwalletPaymentConverterMock);

		verify(testObj).createPayment(any());
	}

	@Test
	void isInvoiceCreated_isCreated_whenPaymentsReceivedContainsOneGoodStatus() {
		mockPaymentNotificationConfig();

		final HyperwalletList<HyperwalletPayment> response = new HyperwalletList<>();
		final List<HyperwalletPayment> dataWithOneCorrectStatus = Stream.of("FAILED", "NO_FAILURE_STATUS", "RETURNED")
				.map(status -> new HyperwalletPayment().status(status)).collect(Collectors.toUnmodifiableList());
		response.setData(dataWithOneCorrectStatus);
		when(paymentHyperwalletApiClient.listPayments(PROGRAM_TOKEN, PAYMENT_ID)).thenReturn(response);

		boolean result = testObj.isInvoiceCreated(defaultHyperwalletPayment());

		Assertions.assertTrue(result, "The invoice seems not created");
	}

	@Test
	void isInvoiceCreated_isNotCreated_whenAllPaymentsReceivedContainsFailStatus() {
		mockPaymentNotificationConfig();

		final HyperwalletList<HyperwalletPayment> response = new HyperwalletList<>();
		final List<HyperwalletPayment> dataWithAllFailures = Stream.of("FAILED", "RECALLED", "RETURNED")
				.map(status -> new HyperwalletPayment().status(status)).collect(Collectors.toUnmodifiableList());
		response.setData(dataWithAllFailures);
		when(paymentHyperwalletApiClient.listPayments(PROGRAM_TOKEN, PAYMENT_ID)).thenReturn(response);

		boolean result = testObj.isInvoiceCreated(defaultHyperwalletPayment());

		Assertions.assertFalse(result, "The invoice seems created");
	}

	private static HyperwalletPayment defaultHyperwalletPayment() {
		final HyperwalletPayment payment = new HyperwalletPayment().programToken(PROGRAM_TOKEN)
				.clientPaymentId(PAYMENT_ID);
		return payment;
	}

	private void mockPaymentNotificationConfig() {
		when(paymentNotificationConfig.getFailureStatuses())
				.thenReturn(Set.of("FAILED", "RECALLED", "RETURNED", "EXPIRED"));
	}

}
