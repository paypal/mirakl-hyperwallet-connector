package com.paypal.invoices.invoicesextract.converter;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklShopToAccountingModelConverterTest {

	private final static String SHOP_ID = "2000";

	@Spy
	@InjectMocks
	private MiraklShopToAccountingModelConverter testObj;

	@Mock
	private InvoiceModel.Builder invoiceModelBuilderMock;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklAdditionalFieldValue miraklAdditionalFieldValueOneMock;

	@Mock
	private InvoiceModel invoiceModelMock;

	@Test
	void convert_shouldReturnInvoiceModelBasedOnValuesOfMiraklShop() {
		when(miraklShopMock.getId()).thenReturn(SHOP_ID);
		final List<MiraklAdditionalFieldValue> miraklAdditionalFieldValues = List.of(miraklAdditionalFieldValueOneMock);
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(miraklAdditionalFieldValues);
		doReturn(invoiceModelBuilderMock).when(testObj).getBuilder();
		when(invoiceModelBuilderMock.shopId(SHOP_ID)).thenReturn(invoiceModelBuilderMock);
		when(invoiceModelBuilderMock.destinationToken(miraklAdditionalFieldValues)).thenReturn(invoiceModelBuilderMock);
		when(invoiceModelBuilderMock.hyperwalletProgram(miraklAdditionalFieldValues))
				.thenReturn(invoiceModelBuilderMock);
		when(invoiceModelBuilderMock.build()).thenReturn(invoiceModelMock);

		final AccountingDocumentModel result = testObj.convert(miraklShopMock);

		verify(invoiceModelBuilderMock).destinationToken(miraklAdditionalFieldValues);
		verify(invoiceModelBuilderMock).shopId(SHOP_ID);

		assertThat(result).isEqualTo(invoiceModelMock);
	}

	@Test
	void getBuilder_shouldReturnAnInvoiceModelBuilderInstance() {
		final AccountingDocumentModel.Builder builder = testObj.getBuilder();

		assertThat(builder).isInstanceOf(AccountingDocumentModel.Builder.class);
	}

}
