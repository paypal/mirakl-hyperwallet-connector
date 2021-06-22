package com.paypal.sellers.sellersextract.converter.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Converts from internal {@link BusinessStakeHolderModel} to
 * {@link HyperwalletBusinessStakeholder}
 */
@Service
public class BusinessStakeHolderModelToHyperWalletBusinessStakeHolderConverter
		implements Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> {

	/**
	 * Method that retrieves a {@link BusinessStakeHolderModel} and returns a
	 * {@link HyperwalletBusinessStakeholder}
	 * @param source the source object {@link BusinessStakeHolderModel}
	 * @return the returned object {@link HyperwalletBusinessStakeholder}
	 */
	@Override
	public HyperwalletBusinessStakeholder convert(final BusinessStakeHolderModel source) {
		final HyperwalletBusinessStakeholder hyperwalletBusinessStakeholder = new HyperwalletBusinessStakeholder();
		hyperwalletBusinessStakeholder.token(source.getToken());
		hyperwalletBusinessStakeholder.setIsBusinessContact(source.getBusinessContact());
		hyperwalletBusinessStakeholder.isDirector(source.getDirector());
		hyperwalletBusinessStakeholder.isUltimateBeneficialOwner(source.getUbo());
		hyperwalletBusinessStakeholder.isSeniorManagingOfficial(source.getSmo());
		hyperwalletBusinessStakeholder.profileType(HyperwalletBusinessStakeholder.ProfileType.INDIVIDUAL);
		hyperwalletBusinessStakeholder.setFirstName(source.getFirstName());
		hyperwalletBusinessStakeholder.setMiddleName(source.getMiddleName());
		hyperwalletBusinessStakeholder.setLastName(source.getLastName());

		Optional.ofNullable(source.getDateOfBirth()).ifPresent(hyperwalletBusinessStakeholder::setDateOfBirth);

		hyperwalletBusinessStakeholder.countryOfBirth(source.getCountryOfBirth());
		hyperwalletBusinessStakeholder.setCountryOfNationality(source.getCountryOfNationality());

		Optional.ofNullable(source.getGender()).ifPresent(gender -> hyperwalletBusinessStakeholder
				.setGender(EnumUtils.getEnum(HyperwalletBusinessStakeholder.Gender.class, gender.name())));

		hyperwalletBusinessStakeholder.setPhoneNumber(source.getPhoneNumber());
		hyperwalletBusinessStakeholder.setMobileNumber(source.getMobileNumber());
		hyperwalletBusinessStakeholder.setEmail(source.getEmail());
		hyperwalletBusinessStakeholder.setGovernmentId(source.getGovernmentId());

		Optional.ofNullable(source.getGovernmentIdType()).ifPresent(
				sellerGovernmentIdType -> hyperwalletBusinessStakeholder.setGovernmentIdType(EnumUtils.getEnum(
						HyperwalletBusinessStakeholder.GovernmentIdType.class, sellerGovernmentIdType.name())));

		hyperwalletBusinessStakeholder.setDriversLicenseId(source.getDriversLicenseId());
		hyperwalletBusinessStakeholder.setAddressLine1(source.getAddressLine1());
		hyperwalletBusinessStakeholder.setAddressLine2(source.getAddressLine2());
		hyperwalletBusinessStakeholder.setCity(source.getCity());
		hyperwalletBusinessStakeholder.setStateProvince(source.getStateProvince());
		hyperwalletBusinessStakeholder.setCountry(source.getCountry());
		hyperwalletBusinessStakeholder.setPostalCode(source.getPostalCode());

		return hyperwalletBusinessStakeholder;
	}

}
