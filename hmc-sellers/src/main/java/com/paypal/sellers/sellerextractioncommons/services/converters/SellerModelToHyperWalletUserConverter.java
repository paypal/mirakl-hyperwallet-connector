package com.paypal.sellers.sellerextractioncommons.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerModelToHyperWalletUserConverter implements Converter<SellerModel, HyperwalletUser> {

	protected final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration;

	@Value("${hmc.toggle-features.new-user-mapping}")
	private boolean newUserMappingEnabled;

	public SellerModelToHyperWalletUserConverter(
			final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration) {
		this.hyperwalletProgramsConfiguration = hyperwalletProgramsConfiguration;
	}

	/**
	 * Method that retrieves a {@link SellerModel} and returns a {@link HyperwalletUser}
	 * @param sellerModel the source object {@link SellerModel}
	 * @return the returned object {@link HyperwalletUser}
	 */
	@Override
	public HyperwalletUser convert(final SellerModel sellerModel) {
		final HyperwalletUser hyperwalletUser = new HyperwalletUser();
		final HyperwalletUser.ProfileType profileType = EnumUtils.getEnum(HyperwalletUser.ProfileType.class,
				sellerModel.getProfileType().name(), HyperwalletUser.ProfileType.UNKNOWN);

		hyperwalletUser.setClientUserId(sellerModel.getClientUserId());
		hyperwalletUser.setProfileType(profileType);
		Optional.ofNullable(sellerModel.getBusinessType()).map(Enum::name).map(HyperwalletUser.BusinessType::valueOf)
				.ifPresent(hyperwalletUser::setBusinessType);
		hyperwalletUser.setAddressLine1(sellerModel.getAddressLine1());
		hyperwalletUser.setCity(sellerModel.getCity());
		hyperwalletUser.setStateProvince(sellerModel.getStateProvince());
		hyperwalletUser.setPostalCode(sellerModel.getPostalCode());
		hyperwalletUser.setCountry(sellerModel.getCountry());
		hyperwalletUser.setProgramToken(hyperwalletProgramsConfiguration
				.getProgramConfiguration(sellerModel.getHyperwalletProgram()).getUsersProgramToken());
		hyperwalletUser.setToken(sellerModel.getToken());
		hyperwalletUser.setEmail(sellerModel.getEmail());

		if (HyperwalletUser.ProfileType.BUSINESS.equals(profileType)) {
			hyperwalletUser.setBusinessRegistrationCountry(sellerModel.getCompanyRegistrationCountry());
			hyperwalletUser.setBusinessRegistrationStateProvince(sellerModel.getBusinessRegistrationStateProvince());
			hyperwalletUser.setBusinessRegistrationId(sellerModel.getCompanyRegistrationNumber());

			if (newUserMappingEnabled) {
				hyperwalletUser.setBusinessName(sellerModel.getCompanyName());
				hyperwalletUser.setBusinessOperatingName(sellerModel.getBusinessName());
			}
			else {
				hyperwalletUser.setBusinessName(sellerModel.getBusinessName());
				hyperwalletUser.setBusinessOperatingName(sellerModel.getCompanyName());
			}
		}

		if (HyperwalletUser.ProfileType.INDIVIDUAL.equals(profileType)) {
			hyperwalletUser.setBusinessName(sellerModel.getBusinessName());
			hyperwalletUser.setFirstName(sellerModel.getFirstName());
			hyperwalletUser.setLastName(sellerModel.getLastName());
			hyperwalletUser.setDateOfBirth(sellerModel.getDateOfBirth());
			hyperwalletUser.setCountryOfBirth(sellerModel.getCountryOfBirth());
			hyperwalletUser.setCountryOfNationality(sellerModel.getCountryOfNationality());
			hyperwalletUser.setPhoneNumber(sellerModel.getPhoneNumber());
			hyperwalletUser.setMobileNumber(sellerModel.getMobilePhone());
			hyperwalletUser.setAddressLine2(sellerModel.getAddressLine2());
			hyperwalletUser.setGovernmentId(sellerModel.getGovernmentId());
			hyperwalletUser.setPassportId(sellerModel.getPassportId());
			hyperwalletUser.setDriversLicenseId(sellerModel.getDriversLicenseId());

			Optional.ofNullable(sellerModel.getGovernmentIdType()).map(Enum::name)
					.map(HyperwalletUser.GovernmentIdType::valueOf).ifPresent(hyperwalletUser::setGovernmentIdType);
		}

		return hyperwalletUser;
	}

}
