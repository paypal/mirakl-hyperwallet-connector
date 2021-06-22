package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.strategy.StrategyFactory;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;

import java.util.List;

public abstract class AbstractMiraklShopToSellerModelConverter implements Strategy<MiraklShop, SellerModel> {

	private final StrategyFactory<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyFactory;

	protected AbstractMiraklShopToSellerModelConverter(
			final StrategyFactory<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyFactory) {
		this.miraklShopBankAccountModelStrategyFactory = miraklShopBankAccountModelStrategyFactory;
	}

	protected SellerModel.SellerModelBuilder getCommonFieldsBuilder(final MiraklShop source) {
		final MiraklContactInformation contactInformation = source.getContactInformation();
		final List<MiraklAdditionalFieldValue> additionalFieldValues = source.getAdditionalFieldValues();
		final BankAccountModel bankAccountModel = miraklShopBankAccountModelStrategyFactory.execute(source);
		//@formatter:off
		return SellerModel.builder()
				.clientUserId(source.getId())
				.businessName(source.getName())
				.firstName(contactInformation.getFirstname())
				.lastName(contactInformation.getLastname())
				.phoneNumber(contactInformation.getPhone())
				.mobilePhone(contactInformation.getPhoneSecondary())
				.email(contactInformation.getEmail())
				.addressLine1(contactInformation.getStreet1())
				.addressLine2(contactInformation.getStreet2())
				.city(contactInformation.getCity())
				.postalCode(contactInformation.getZipCode())
				.stateProvince(contactInformation.getState())
				.country(contactInformation.getCountry())
				.dateOfBirth(additionalFieldValues)
				.passportId(additionalFieldValues)
				.countryOfBirth(additionalFieldValues)
				.countryOfNationality(additionalFieldValues)
				.governmentId(additionalFieldValues)
				.governmentIdType(additionalFieldValues)
				.driversLicenseId(additionalFieldValues)
				.employerId(additionalFieldValues)
				.businessType(additionalFieldValues)
				.token(additionalFieldValues)
				.bankAccountDetails(bankAccountModel)
				.hwTermsConsent(additionalFieldValues)
				.hyperwalletProgram(additionalFieldValues);
		//@formatter:on
	}

}
