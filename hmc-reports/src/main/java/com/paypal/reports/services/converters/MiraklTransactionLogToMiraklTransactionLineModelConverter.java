package com.paypal.reports.services.converters;

import com.mirakl.client.mmp.operator.domain.payment.transaction.MiraklTransactionLine;
import com.mirakl.client.mmp.operator.domain.payment.transaction.MiraklTransactionShop;
import com.mirakl.client.mmp.operator.domain.payment.transaction.entity.MiraklTransactionEntity;
import com.mirakl.client.mmp.operator.domain.payment.transaction.entity.MiraklTransactionInfo;
import com.mirakl.client.mmp.operator.domain.payment.transaction.entity.MiraklTransactionOrder;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Optional;

/**
 * Converts {@link MiraklTransactionLine} to {@link HmcMiraklTransactionLine}
 */
@Slf4j
@Service
public class MiraklTransactionLogToMiraklTransactionLineModelConverter
		implements Converter<MiraklTransactionLine, HmcMiraklTransactionLine> {

	@Override
	public HmcMiraklTransactionLine convert(final MiraklTransactionLine source) {
		if (Objects.isNull(source)) {
			log.warn("Input MiraklTransactionLine is null");
			return null;
		}
		//@formatter:off
		return HmcMiraklTransactionLine.builder()
				.orderId(getOrderId(source))
				.creditAmount(source.getAmountCredited())
				.debitAmount(source.getAmountDebited())
				.amount(source.getAmount())
				.transactionTime(Objects.nonNull(source.getDateCreated()) ? DateUtil.convertToLocalDateTime(source.getDateCreated()) : null)
				.transactionNumber(getTransactionNumber(source))
				.transactionType(source.getType())
				.transactionLineId(source.getId())
				.currencyIsoCode(String.valueOf(source.getCurrencyIsoCode()))
				.sellerId(getShopId(source))
				.build();
		//@formatter:on
	}

	private String getOrderId(final MiraklTransactionLine source) {
		return Optional.ofNullable(source.getEntities())
			.map(MiraklTransactionEntity::getOrder)
			.map(MiraklTransactionOrder::getId)
			.orElse(StringUtils.EMPTY);
	}

	private String getTransactionNumber(final MiraklTransactionLine source) {
		return Optional.ofNullable(source.getEntities())
			.map(MiraklTransactionEntity::getTransactionInfo)
			.map(MiraklTransactionInfo::getNumber)
			.orElse(StringUtils.EMPTY);
	}

	private String getShopId(final MiraklTransactionLine source) {
		return Optional.ofNullable(source.getShop()).map(MiraklTransactionShop::getId).orElse(StringUtils.EMPTY);
	}

}
