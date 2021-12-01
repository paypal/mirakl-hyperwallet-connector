package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractInvoiceExtractServiceImplTest {

	private MyInvoiceExtractService testObj;

	@Mock
	private MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractServiceMock;

	@Mock
	private MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractServiceMock;

	@Mock
	private HyperWalletPaymentExtractService hyperWalletPaymentExtractServiceMock;

	@Mock
	private InvoiceModel payeeInvoiceModelOneMock, payeeInvoiceModelTwoMock, operatorInvoiceModelOneMock,
			operatorInvoiceModelTwoMock;

	@Mock
	private CreditNoteModel creditNoteOneMock, creditNoteTwoMock;

	@Mock
	private HyperwalletPayment payeeHyperWalletPaymentOneMock, payeeHyperWalletPaymentTwoMock,
			operatorHyperWalletPaymentOneMock, operatorHyperWalletPaymentTwoMock, creditNoteHyperWalletOneMock,
			creditNoteHyperWalletTwoMock;

	@Test
	void extractInvoices_shouldCallMiraklInvoiceExtractServiceAndHyperwalletPaymentExtractServiceAndCollectInvoicesPaidForOperatorAndPayeeWithDeltaPassedAsArgument() {
		testObj = new MyInvoiceExtractService(miraklAccountingDocumentInvoicesExtractServiceMock,
				miraklAccountingDocumentCreditNotesExtractServiceMock, hyperWalletPaymentExtractServiceMock,
				List.of(operatorHyperWalletPaymentOneMock, operatorHyperWalletPaymentTwoMock));

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());
		final List<InvoiceModel> invoices = List.of(payeeInvoiceModelOneMock, payeeInvoiceModelTwoMock,
				operatorInvoiceModelOneMock, operatorInvoiceModelTwoMock);
		doReturn(invoices).when(miraklAccountingDocumentInvoicesExtractServiceMock).extractAccountingDocument(delta);
		final List<HyperwalletPayment> payeeCreatedPayments = List.of(payeeHyperWalletPaymentOneMock,
				payeeHyperWalletPaymentTwoMock);
		when(hyperWalletPaymentExtractServiceMock.payPayeeInvoice(invoices)).thenReturn(payeeCreatedPayments);

		final List<HyperwalletPayment> result = testObj.extractInvoices(delta);

		verify(miraklAccountingDocumentInvoicesExtractServiceMock).extractAccountingDocument(delta);
		verify(hyperWalletPaymentExtractServiceMock).payPayeeInvoice(invoices);

		assertThat(result).containsExactlyInAnyOrder(payeeHyperWalletPaymentOneMock, payeeHyperWalletPaymentTwoMock,
				operatorHyperWalletPaymentOneMock, operatorHyperWalletPaymentTwoMock);
	}

	@Test
	void extractCreditNotes_shouldCallMiraklInvoiceExtractServiceAndHyperwalletPaymentExtractServiceAndCollectCreditNotesWithDeltaPassedAsArgument() {
		testObj = new MyInvoiceExtractService(miraklAccountingDocumentInvoicesExtractServiceMock,
				miraklAccountingDocumentCreditNotesExtractServiceMock, hyperWalletPaymentExtractServiceMock, List.of());

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());
		final List<CreditNoteModel> creditNotes = List.of(creditNoteOneMock, creditNoteTwoMock);
		when(miraklAccountingDocumentCreditNotesExtractServiceMock.extractAccountingDocument(delta))
				.thenReturn(creditNotes);
		final List<HyperwalletPayment> creditNotesCreatedPayments = List.of(creditNoteHyperWalletOneMock,
				creditNoteHyperWalletTwoMock);
		when(hyperWalletPaymentExtractServiceMock.payPayeeCreditNote(creditNotes))
				.thenReturn(creditNotesCreatedPayments);

		final List<HyperwalletPayment> result = testObj.extractCreditNotes(delta);

		verify(miraklAccountingDocumentCreditNotesExtractServiceMock).extractAccountingDocument(delta);
		verify(hyperWalletPaymentExtractServiceMock).payPayeeCreditNote(creditNotes);

		assertThat(result).containsExactlyInAnyOrder(creditNoteHyperWalletOneMock, creditNoteHyperWalletTwoMock);
	}

	private static class MyInvoiceExtractService extends AbstractInvoiceExtractService {

		final List<HyperwalletPayment> operatorInvoices;

		protected MyInvoiceExtractService(
				final MiraklAccountingDocumentExtractService<InvoiceModel> miraklInvoiceExtractService,
				final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklCreditNotesExtractService,
				final HyperWalletPaymentExtractService hyperWalletPaymentExtractService,
				final List<HyperwalletPayment> operatorInvoices) {
			super(miraklInvoiceExtractService, miraklCreditNotesExtractService, hyperWalletPaymentExtractService);
			this.operatorInvoices = operatorInvoices;
		}

		@Override
		protected List<HyperwalletPayment> payOperator(final List<InvoiceModel> invoices) {
			return operatorInvoices;
		}

	}

}
