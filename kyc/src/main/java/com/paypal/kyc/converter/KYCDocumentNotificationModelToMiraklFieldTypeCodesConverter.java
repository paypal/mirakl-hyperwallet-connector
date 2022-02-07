package com.paypal.kyc.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCDocumentNotificationModel;
import com.paypal.kyc.model.KYCProofOfAddressEnum;
import com.paypal.kyc.model.KYCProofOfBusinessEnum;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Converts {@link KYCDocumentNotificationModel} into its corresponding Mirakl fields
 */
@Slf4j
@Service
public class KYCDocumentNotificationModelToMiraklFieldTypeCodesConverter
		implements Converter<KYCDocumentNotificationModel, List<String>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> convert(final KYCDocumentNotificationModel document) {
		final KYCProofOfIdentityEnum kycProofOfIdentity = EnumUtils.getEnum(KYCProofOfIdentityEnum.class,
				document.getDocumentType().toString());
		final KYCProofOfAddressEnum kycProofOfAddress = EnumUtils.getEnum(KYCProofOfAddressEnum.class,
				document.getDocumentType().toString());
		final KYCProofOfBusinessEnum kycProofOfBusiness = EnumUtils.getEnum(KYCProofOfBusinessEnum.class,
				document.getDocumentType().toString());

		if (Optional.ofNullable(kycProofOfAddress).isPresent()) {
			return KYCProofOfAddressEnum.getMiraklFields();
		}
		else if (Optional.ofNullable(kycProofOfIdentity).isPresent()) {
			return KYCProofOfIdentityEnum.getMiraklFields(kycProofOfIdentity);
		}
		else if (Optional.ofNullable(kycProofOfBusiness).isPresent()) {
			return KYCProofOfBusinessEnum.getMiraklFields();
		}

		return List.of();
	}

}
