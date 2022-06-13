package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountingDocumentBatchJobItemEnricherTest extends AccountingDocumentBatchJobHandlersTestSupport {

	@InjectMocks
	@Spy
	private AccountingDocumentBatchJobItemEnricher testObj;

	@Mock
	private AccountingDocumentsLinksService accountingDocumentsLinksServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private InvoiceModel invoiceModelMock, enrichedInvoiceModelMock;

	@Mock
	private TestAccountingDocumentBatchJobItem testAccountingDocumentBatchJobItemMock,
			enrichedTestAccountingDocumentBatchJobItemMock;

	@Mock
	private HyperwalletItemLinkLocator hyperwalletItemLinkLocator1Mock, hyperwalletItemLinkLocator2Mock;

	@Test
	void enrichItem_shouldAddBankAccountAndProgramToken() {
		when(testAccountingDocumentBatchJobItemMock.getItem()).thenReturn(invoiceModelMock);

		when(accountingDocumentsLinksServiceMock.findRequiredLinks(invoiceModelMock))
				.thenReturn(List.of(hyperwalletItemLinkLocator1Mock, hyperwalletItemLinkLocator2Mock));
		when(hyperwalletItemLinkLocator1Mock.getType()).thenReturn(HyperwalletItemTypes.BANK_ACCOUNT);
		when(hyperwalletItemLinkLocator1Mock.getId()).thenReturn("B");
		when(hyperwalletItemLinkLocator2Mock.getType()).thenReturn(HyperwalletItemTypes.PROGRAM);
		when(hyperwalletItemLinkLocator2Mock.getId()).thenReturn("P");

		when(testAccountingDocumentBatchJobItemMock.from(enrichedInvoiceModelMock))
				.thenReturn(enrichedTestAccountingDocumentBatchJobItemMock);
		doReturn(enrichedInvoiceModelMock).when(testObj).buildEnriched(invoiceModelMock, "B", "P");

		TestAccountingDocumentBatchJobItem result = (TestAccountingDocumentBatchJobItem) testObj
				.enrichItem(batchJobContextMock, testAccountingDocumentBatchJobItemMock);

		assertThat(result).isEqualTo(enrichedTestAccountingDocumentBatchJobItemMock);
	}

}
