package com.paypal.reports.model;

import com.paypal.infrastructure.support.date.DateUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class HmcFinancialReportLine {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private String braintreeCommerceOrderId;

	private String miraklOrderId;

	private String miraklSellerId;

	private String miraklTransactionLineId;

	private LocalDateTime miraklTransactionTime;

	private String miraklTransactionType;

	private String currencyIsoCode;

	private BigDecimal miraklCreditAmount;

	private BigDecimal miraklDebitAmount;

	private BigDecimal braintreeAmount;

	private String braintreeTransactionId;

	private LocalDateTime braintreeTransactionTime;

	@Override
	public String toString() {
		//@formatter:off
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append("braintreeCommerceOrderId", Objects.nonNull(braintreeCommerceOrderId) ? braintreeCommerceOrderId : StringUtils.EMPTY)
				.append("miraklOrderId", Objects.nonNull(miraklOrderId) ? miraklOrderId : StringUtils.EMPTY)
				.append("miraklSellerId", Objects.nonNull(miraklSellerId) ? miraklSellerId : StringUtils.EMPTY)
				.append("miraklTransactionLineId", Objects.nonNull(miraklTransactionLineId) ? miraklTransactionLineId : StringUtils.EMPTY)
				.append("miraklTransactionTime", Objects.nonNull(miraklTransactionTime) ? DateUtil.convertToString(miraklTransactionTime, DATE_FORMAT) : StringUtils.EMPTY)
				.append("miraklTransactionType", Objects.nonNull(miraklTransactionType) ? miraklTransactionType : StringUtils.EMPTY)
				.append("braintreeAmount", Objects.nonNull(braintreeAmount) ? braintreeAmount : BigDecimal.ZERO)
				.append("miraklDebitAmount", Objects.nonNull(miraklDebitAmount) ? String.valueOf(miraklDebitAmount) : StringUtils.EMPTY)
				.append("miraklCreditAmount", Objects.nonNull(miraklCreditAmount) ? String.valueOf(miraklCreditAmount) : StringUtils.EMPTY)
				.append("currencyIsoCode", Objects.nonNull(currencyIsoCode) ? currencyIsoCode : StringUtils.EMPTY)
				.append("braintreeTransactionId", Objects.nonNull(braintreeTransactionId) ? braintreeTransactionId : StringUtils.EMPTY)
				.append("braintreeTransactionTime", Objects.nonNull(braintreeTransactionTime) ? DateUtil.convertToString(braintreeTransactionTime, DATE_FORMAT) : StringUtils.EMPTY)
				.toString();
		//@formatter:on
	}

}
