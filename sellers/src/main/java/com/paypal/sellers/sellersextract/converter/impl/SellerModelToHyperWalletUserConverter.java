package com.paypal.sellers.sellersextract.converter.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.sellers.infrastructure.configuration.SellersHyperwalletApiConfig;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerModelToHyperWalletUserConverter implements Converter<SellerModel, HyperwalletUser> {

	protected final SellersHyperwalletApiConfig sellersHyperwalletApiConfig;

	public SellerModelToHyperWalletUserConverter(final SellersHyperwalletApiConfig sellersHyperwalletApiConfig) {
		this.sellersHyperwalletApiConfig = sellersHyperwalletApiConfig;
	}

	/**
	 * Method that retrieves a {@link SellerModel} and returns a {@link HyperwalletUser}
	 * @param sellerModel the source object {@link SellerModel}
	 * @return the returned object {@link HyperwalletUser}
	 */
	@Override
	public HyperwalletUser convert(final SellerModel sellerModel) {
		//@formatter:off
		final HyperwalletUser hyperwalletUser = new HyperwalletUser();
		hyperwalletUser.setClientUserId(sellerModel.getClientUserId());
		hyperwalletUser.setBusinessName(sellerModel.getBusinessName());
		hyperwalletUser.setProfileType(EnumUtils.getEnum(HyperwalletUser.ProfileType.class,
				sellerModel.getProfileType().name(), HyperwalletUser.ProfileType.UNKNOWN));
		hyperwalletUser.setFirstName(sellerModel.getFirstName());
		hyperwalletUser.setLastName(sellerModel.getLastName());
		hyperwalletUser.setDateOfBirth(sellerModel.getDateOfBirth());
		hyperwalletUser.setCountryOfBirth(sellerModel.getCountryOfBirth());
		hyperwalletUser.setCountryOfNationality(sellerModel.getCountryOfNationality());
		hyperwalletUser.setPhoneNumber(sellerModel.getPhoneNumber());
		hyperwalletUser.setMobileNumber(sellerModel.getMobilePhone());
		hyperwalletUser.setEmail(sellerModel.getEmail());
		hyperwalletUser.setGovernmentId(sellerModel.getGovernmentId());
		hyperwalletUser.setPassportId(sellerModel.getPassportId());
		hyperwalletUser.setEmployerId(sellerModel.getEmployerId());
		hyperwalletUser.setAddressLine1(sellerModel.getAddressLine1());
		hyperwalletUser.setAddressLine2(sellerModel.getAddressLine2());
		hyperwalletUser.setCity(sellerModel.getCity());
		hyperwalletUser.setStateProvince(sellerModel.getStateProvince());
		hyperwalletUser.setCountry(sellerModel.getCountry());
		hyperwalletUser.setPostalCode(sellerModel.getPostalCode());
		hyperwalletUser.setProgramToken(Optional.ofNullable(sellersHyperwalletApiConfig.getUserStoreTokens().get(sellerModel.getHyperwalletProgram())).orElse(null));
		hyperwalletUser.setDriversLicenseId(sellerModel.getDriversLicenseId());
		hyperwalletUser.setToken(sellerModel.getToken());
		hyperwalletUser.setBusinessOperatingName(Optional.ofNullable(sellerModel.getCompanyName()).orElse(null));
		hyperwalletUser.setBusinessRegistrationId(Optional.ofNullable(sellerModel.getCompanyRegistrationNumber()).orElse(null));
		hyperwalletUser.setBusinessRegistrationCountry(Optional.ofNullable(sellerModel.getCompanyRegistrationCountry()).orElse(null));
		hyperwalletUser.setBusinessRegistrationStateProvince(Optional.ofNullable(sellerModel.getBusinessRegistrationStateProvince()).orElse(null));

		Optional.ofNullable(sellerModel.getBusinessType())
                .map(Enum::name)
                .map(HyperwalletUser.BusinessType::valueOf)
                .ifPresent(hyperwalletUser::setBusinessType);
        Optional.ofNullable(sellerModel.getGovernmentIdType())
                .map(Enum::name)
                .map(HyperwalletUser.GovernmentIdType::valueOf)
                .ifPresent(hyperwalletUser::setGovernmentIdType);
        //@formatter:on

		return hyperwalletUser;
	}

}
