package com.paypal.kyc.stakeholdersdocumentextraction.services.converters;

import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.support.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;

/***
 * Converts from Date to MiraklGetShopsRequest
 */
@Service
public class DateToMiraklGetShopsRequestConverter implements Converter<Date, MiraklGetShopsRequest> {

	@Override
	public MiraklGetShopsRequest convert(@NonNull final Date source) {
		final MiraklGetShopsRequest miraklGetShopsRequest = new MiraklGetShopsRequest();
		miraklGetShopsRequest.setUpdatedSince(source);
		miraklGetShopsRequest.setPaginate(false);

		return miraklGetShopsRequest;
	}

}
