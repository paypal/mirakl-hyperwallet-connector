package com.paypal.invoices.invoicesextract.converter;

import com.paypal.invoices.dto.MiraklInvoiceMockDTO;
import com.paypal.invoices.dto.MiraklInvoicesMockListDTO;
import com.paypal.invoices.dto.converter.MiraklInvoiceDTOToInvoiceModelConverter;
import com.paypal.invoices.dto.converter.MiraklInvoiceListDTOToInvoiceModelListConverter;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklInvoiceListDTOToInvoiceModelListConverterTest {

	@InjectMocks
	private MiraklInvoiceListDTOToInvoiceModelListConverter testObj;

	@Mock
	private MiraklInvoiceMockDTO miraklInvoiceMockDTOOneMock, miraklInvoiceMockDTOTwoMock;

	@Mock
	private InvoiceModel invoiceModelOneMock, invoiceModelTwoMock;

	@Mock
	private MiraklInvoicesMockListDTO miraklInvoicesMockListDTOMock;

	@Mock
	private MiraklInvoiceDTOToInvoiceModelConverter miraklInvoiceDTOToInvoiceModelConverterMock;

	@Test
	void convert_shouldReturnListOfInvoicesModelWhenListOfMiraklInvoiceListDTOIsReceivedAndNotEmpty() {
		when(miraklInvoicesMockListDTOMock.getInvoices())
				.thenReturn(List.of(miraklInvoiceMockDTOOneMock, miraklInvoiceMockDTOTwoMock));

		when(miraklInvoiceDTOToInvoiceModelConverterMock.convert(miraklInvoiceMockDTOOneMock))
				.thenReturn(invoiceModelOneMock);
		when(miraklInvoiceDTOToInvoiceModelConverterMock.convert(miraklInvoiceMockDTOTwoMock))
				.thenReturn(invoiceModelTwoMock);

		final List<InvoiceModel> result = testObj.convert(miraklInvoicesMockListDTOMock);

		Assertions.assertThat(result).hasSize(2).containsExactlyInAnyOrder(invoiceModelOneMock, invoiceModelTwoMock);
	}

	@Test
	void convert_shouldReturnEmptyListWhenNullParameterIsReceived() {

		final List<InvoiceModel> result = testObj.convert(null);

		Assertions.assertThat(result).isEqualTo(Collections.emptyList());
	}

}
