package com.paypal.reports.services.impl;

import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import com.paypal.reports.model.HmcFinancialReportLine;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FinancialReportConverterServiceImplTest {

	private FinancialReportConverterServiceImpl testObj;

	@Mock
	private Converter<HmcBraintreeTransactionLine, HmcFinancialReportLine> hmcBraintreeTransactionLineHmcFinancialReportLineConverterMock;

	@Mock
	private Converter<HmcMiraklTransactionLine, HmcFinancialReportLine> hmcMiraklTransactionLineHmcFinancialReportLineConverterMock;

	@Mock
	private Converter<Pair<HmcBraintreeTransactionLine, HmcMiraklTransactionLine>, HmcFinancialReportLine> hmcFinancialReportLineConverterMock;

	@Mock
	private HmcBraintreeTransactionLine hmcBraintreeTransactionLineMock;

	@Mock
	private HmcMiraklTransactionLine hmcMiraklTransactionLineMock;

	@BeforeEach
	void setUp() {
		testObj = new FinancialReportConverterServiceImpl(
				hmcBraintreeTransactionLineHmcFinancialReportLineConverterMock,
				hmcMiraklTransactionLineHmcFinancialReportLineConverterMock, hmcFinancialReportLineConverterMock);
	}

	@Test
	void convertBraintreeTransactionLineIntoFinancialReportLine_shouldCallToBraintreeTransactionLineToFinancialReportLineConverter() {
		testObj.convertBraintreeTransactionLineIntoFinancialReportLine(hmcBraintreeTransactionLineMock);

		verify(hmcBraintreeTransactionLineHmcFinancialReportLineConverterMock).convert(hmcBraintreeTransactionLineMock);
	}

	@Test
	void convertMiraklTransactionLineIntoFinancialReportLine_shouldCallToMiraklTransactionLineToFinancialReportLineConverter() {
		testObj.convertMiraklTransactionLineIntoFinancialReportLine(hmcMiraklTransactionLineMock);

		verify(hmcMiraklTransactionLineHmcFinancialReportLineConverterMock).convert(hmcMiraklTransactionLineMock);
	}

	@Test
	void convertBrainTreeAndMiraklTransactionLineIntoFinancialReportLine_shouldCallToBraintreeAndMiraklTransactionLineToFinancialReportLineConverter() {
		testObj.convertBrainTreeAndMiraklTransactionLineIntoFinancialReportLine(hmcBraintreeTransactionLineMock,
				hmcMiraklTransactionLineMock);

		verify(hmcFinancialReportLineConverterMock)
				.convert(Pair.of(hmcBraintreeTransactionLineMock, hmcMiraklTransactionLineMock));
	}

}
