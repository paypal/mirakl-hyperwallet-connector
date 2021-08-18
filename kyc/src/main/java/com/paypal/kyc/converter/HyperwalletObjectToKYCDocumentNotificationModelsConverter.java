package com.paypal.kyc.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converts Documents Notification object into {@link KYCDocumentNotificationModel} list
 */
@Slf4j
@Service
public class HyperwalletObjectToKYCDocumentNotificationModelsConverter
		implements Converter<Object, List<KYCDocumentNotificationModel>> {

	@Override
	public List<KYCDocumentNotificationModel> convert(final Object notificationObject) {
		if (notificationObject instanceof Map) {
			final Map<String, Object> notificationDetails = (Map<String, Object>) notificationObject;
			final List<Map<String, Object>> documents = Optional
					.ofNullable((List<Map<String, Object>>) notificationDetails.get("documents"))
					.orElse(Collections.emptyList());

			return documents.stream().map(document -> KYCDocumentNotificationModel.builder()
					.documentStatus(EnumUtils.getEnum(KYCDocumentStatusEnum.class, (String) document.get("status")))
					.documentType(EnumUtils.getEnum(KYCDocumentTypeEnum.class, (String) document.get("type")))
					.createdOn(LocalDateTime.parse((String) document.get("createdOn")))
					.documentCategory(
							EnumUtils.getEnum(KYCDocumentCategoryEnum.class, (String) document.get("category")))
					.documentRejectedReasons(getRejectedReasons(
							Optional.ofNullable((List<Map<String, String>>) document.get("reasons")).orElse(List.of())))
					.build()).collect(Collectors.toList());
		}
		return List.of();
	}

	private List<KYCDocumentRejectedReasonEnum> getRejectedReasons(final List<Map<String, String>> reasons) {
		return Optional.ofNullable(reasons).orElseGet(List::of).stream()
				.map(reason -> EnumUtils.getEnum(KYCDocumentRejectedReasonEnum.class, reason.get("name")))
				.collect(Collectors.toList());
	}

}
