package com.paypal.kyc.stakeholdersdocumentextraction.services.converters;

import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.kyc.stakeholdersdocumentextraction.services.converters.DateToMiraklGetShopsRequestConverter;
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
