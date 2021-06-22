package com.paypal.reports.reportsextract.converter;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLog;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionType;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklTransactionLogToMiraklTransactionLineModelConverterTest {

	private static final BigDecimal AMOUNT_CREDITED = BigDecimal.valueOf(20.00D);

	private static final BigDecimal AMOUNT_DEBITED = BigDecimal.valueOf(0.00D);

	private static final BigDecimal AMOUNT = BigDecimal.valueOf(23.00D);

	private static final String TRANSACTION_NUMBER = "mkl-2567478968";

	private static final MiraklTransactionType TRANSACTION_TYPE = MiraklTransactionType.PAYMENT;

	private static final String ID = "12358468";

	private static final MiraklIsoCurrencyCode CURRENCY_ISO_CODE = MiraklIsoCurrencyCode.AUD;

	private static final String SHOP_ID = "2000";

	private static final String ORDER_ID = "231465455445";

	@InjectMocks
	private MiraklTransactionLogToMiraklTransactionLineModelConverter testObj;

	@Test
	void convert_shouldReturnPopulatedHMCMiraklTransactionLineWhenValidMiraklTransactionLogIsPassedAsArgument() {
		final Date now = new Date();
		final LocalDateTime nowLocalDate = DateUtil.convertToLocalDateTime(now);

		final MiraklTransactionLog miraklTransactionLog = new MiraklTransactionLog();
		miraklTransactionLog.setAmountCredited(AMOUNT_CREDITED);
		miraklTransactionLog.setAmountDebited(AMOUNT_DEBITED);
		miraklTransactionLog.setAmount(AMOUNT);
		miraklTransactionLog.setTransactionNumber(TRANSACTION_NUMBER);
		miraklTransactionLog.setTransactionType(TRANSACTION_TYPE);
		miraklTransactionLog.setId(ID);
		miraklTransactionLog.setCurrencyIsoCode(CURRENCY_ISO_CODE);
		miraklTransactionLog.setShopId(SHOP_ID);
		miraklTransactionLog.setDateCreated(now);
		miraklTransactionLog.setOrderId(ORDER_ID);

		final HmcMiraklTransactionLine result = testObj.convert(miraklTransactionLog);

		assertThat(result.getCreditAmount()).isEqualTo(AMOUNT_CREDITED);
		assertThat(result.getDebitAmount()).isEqualTo(AMOUNT_DEBITED);
		assertThat(result.getAmount()).isEqualTo(AMOUNT);
		assertThat(result.getTransactionNumber()).isEqualTo(TRANSACTION_NUMBER);
		assertThat(result.getTransactionType()).isEqualTo("PAYMENT");
		assertThat(result.getTransactionLineId()).isEqualTo(ID);
		assertThat(result.getCurrencyIsoCode()).isEqualTo("AUD");
		assertThat(result.getSellerId()).isEqualTo(SHOP_ID);
		assertThat(result.getTransactionTime()).isEqualTo(nowLocalDate);
		assertThat(result.getOrderId()).isEqualTo(ORDER_ID);
	}

	@Test
	void convert_shouldReturnNullWhenInputArgumentIsNull() {
		final HmcMiraklTransactionLine result = testObj.convert(null);

		assertThat(result).isNull();
	}

}
