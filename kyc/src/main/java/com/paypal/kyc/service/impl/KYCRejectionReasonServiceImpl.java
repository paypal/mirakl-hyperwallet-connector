package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.kyc.model.KYCRejectionReasonTypeEnum;
import com.paypal.kyc.service.KYCRejectionReasonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link KYCRejectionReasonService}
 */
@Slf4j
@Service
public class KYCRejectionReasonServiceImpl implements KYCRejectionReasonService {

	private static final String BUSINESS = "BUSINESS";

	private static final String VERIFICATION_STATUS = "verificationStatus";

	private static final String BUSINESS_STAKEHOLDER_VERIFICATION_STATUS = "businessStakeholderVerificationStatus";

	private static final String LETTER_OF_AUTHORIZATION_STATUS = "letterOfAuthorizationStatus";

	private static final String PROFILE_TYPE = "profileType";

	@Override
	@SuppressWarnings("unchecked")
	public List<KYCRejectionReasonTypeEnum> getReasonTypes(final Object notificationObject) {
		final List<KYCRejectionReasonTypeEnum> reasons = new ArrayList<>();

		if (notificationObject instanceof Map) {
			final Map<String, String> notificationDetails = (Map<String, String>) notificationObject;

			final HyperwalletUser.VerificationStatus verificationStatus = EnumUtils.getEnum(
					HyperwalletUser.VerificationStatus.class,
					Optional.ofNullable(notificationDetails.get(VERIFICATION_STATUS)).orElse(null));

			final HyperwalletUser.BusinessStakeholderVerificationStatus businessStakeholderVerificationStatus = EnumUtils
					.getEnum(HyperwalletUser.BusinessStakeholderVerificationStatus.class,
							Optional.ofNullable(notificationDetails.get(BUSINESS_STAKEHOLDER_VERIFICATION_STATUS))
									.orElse(null));

			final HyperwalletUser.LetterOfAuthorizationStatus letterOfAuthorizationStatus = EnumUtils.getEnum(
					HyperwalletUser.LetterOfAuthorizationStatus.class,
					Optional.ofNullable(notificationDetails.get(LETTER_OF_AUTHORIZATION_STATUS)).orElse(null));

			//@formatter:off
			final boolean isProfessional = Optional.ofNullable(notificationDetails.get(PROFILE_TYPE))
					.orElse(StringUtils.EMPTY)
					.equals(BUSINESS);
			//@formatter:on

			if (HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED
					.equals(businessStakeholderVerificationStatus)) {
				reasons.add(KYCRejectionReasonTypeEnum.BUSINESS_STAKEHOLDER_REQUIRED);
			}

			if (HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED.equals(letterOfAuthorizationStatus)) {
				reasons.add(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED);
			}

			if (HyperwalletUser.VerificationStatus.REQUIRED.equals(verificationStatus)) {
				if (isProfessional) {
					reasons.add(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_PROF_REQUIRED);
				}
				else {
					reasons.add(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_IND_REQUIRED);
				}
			}
		}
		else {
			log.warn("The notification object received is not a Map instance.");
		}

		return reasons;
	}

	@Override
	public String getRejectionReasonDescriptions(final List<KYCRejectionReasonTypeEnum> reasonsType) {
		if (Objects.isNull(reasonsType) || CollectionUtils.isEmpty(reasonsType)) {
			return "";
		}
		final String reasonsDescription = reasonsType.stream().map(KYCRejectionReasonTypeEnum::getReason)
				.collect(Collectors.joining(""));

		return KYCRejectionReasonTypeEnum.getReasonHeader() + reasonsDescription
				+ KYCRejectionReasonTypeEnum.getReasonFooter();
	}

}
