package com.paypal.reports.services.converters;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionType;
import com.mirakl.client.mmp.operator.domain.payment.transaction.MiraklTransactionLine;
import com.mirakl.client.mmp.operator.domain.payment.transaction.MiraklTransactionShop;
import com.mirakl.client.mmp.operator.domain.payment.transaction.entity.MiraklTransactionEntity;
import com.mirakl.client.mmp.operator.domain.payment.transaction.entity.MiraklTransactionInfo;
import com.mirakl.client.mmp.operator.domain.payment.transaction.entity.MiraklTransactionOrder;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklTransactionLineToMiraklTransactionLineModelConverterTest {

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

		final MiraklTransactionLine miraklTransactionLine = new MiraklTransactionLine();
		final MiraklTransactionEntity entity = new MiraklTransactionEntity();
		final MiraklTransactionShop shop = new MiraklTransactionShop();
		final MiraklTransactionOrder order = new MiraklTransactionOrder();
		final MiraklTransactionInfo transactionInfo = new MiraklTransactionInfo();
		transactionInfo.setNumber(TRANSACTION_NUMBER);
		shop.setId(SHOP_ID);
		order.setId(ORDER_ID);
		entity.setTransactionInfo(transactionInfo);
		entity.setOrder(order);
		miraklTransactionLine.setAmountCredited(AMOUNT_CREDITED);
		miraklTransactionLine.setAmountDebited(AMOUNT_DEBITED);
		miraklTransactionLine.setAmount(AMOUNT);
		miraklTransactionLine.setEntities(entity);
		miraklTransactionLine.setType(String.valueOf(TRANSACTION_TYPE));
		miraklTransactionLine.setId(ID);
		miraklTransactionLine.setCurrencyIsoCode(String.valueOf(CURRENCY_ISO_CODE));
		miraklTransactionLine.setShop(shop);
		miraklTransactionLine.setDateCreated(now);

		final HmcMiraklTransactionLine result = testObj.convert(miraklTransactionLine);

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
