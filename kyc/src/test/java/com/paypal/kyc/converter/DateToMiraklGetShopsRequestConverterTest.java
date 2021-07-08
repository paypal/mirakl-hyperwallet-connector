package com.paypal.kyc.converter;

import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class DateToMiraklGetShopsRequestConverterTest {

	private final DateToMiraklGetShopsRequestConverter testObj = new DateToMiraklGetShopsRequestConverter();

	@Test
	void convert_shouldConvertFromDateToMiraklGetShopRequestWhenDateIsNotNullAndEnsurePaginatedIsFalse() {
		final Date date = new Date();

		final MiraklGetShopsRequest result = testObj.convert(date);

		assertThat(result.getUpdatedSince()).isEqualTo(date);
		assertThat(result.isPaginate()).isFalse();
	}

}
