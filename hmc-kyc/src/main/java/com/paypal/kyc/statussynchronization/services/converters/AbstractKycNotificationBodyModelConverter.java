package com.paypal.kyc.statussynchronization.services.converters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.util.HyperwalletJsonConfiguration;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.model.KYCNotificationBodyModel;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

public abstract class AbstractKycNotificationBodyModelConverter<T extends KYCNotificationBodyModel>
		implements Converter<HyperwalletUser, T> {

	private final Converter<Object, T> toNotificationBodyModelConverter;

	protected AbstractKycNotificationBodyModelConverter(final Converter<Object, T> toNotificationBodyModelConverter) {
		this.toNotificationBodyModelConverter = toNotificationBodyModelConverter;
	}

	@Override
	public T convert(@NotNull final HyperwalletUser hyperwalletUser) {
		final ObjectMapper objectMapper = getObjectMapper();
		final Map<String, Object> hyperwalletUserMap = objectMapper.convertValue(hyperwalletUser, Map.class);

		return toNotificationBodyModelConverter.convert(hyperwalletUserMap);
	}

	@SuppressWarnings("java:S1874") // Object mapper code comes from Hyperwallet SDK
	private ObjectMapper getObjectMapper() {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		final SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter(HyperwalletJsonConfiguration.INCLUSION_FILTER,
				SimpleBeanPropertyFilter.serializeAll());

		return new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
				.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
				.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
				.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, false).setDateFormat(dateFormat)
				.setFilterProvider(filterProvider).setSerializationInclusion(JsonInclude.Include.ALWAYS);
	}

}
