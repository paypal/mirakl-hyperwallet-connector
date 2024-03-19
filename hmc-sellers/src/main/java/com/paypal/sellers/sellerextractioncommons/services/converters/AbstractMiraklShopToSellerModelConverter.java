package com.paypal.sellers.sellerextractioncommons.services.converters;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.utils.LanguageConverter;

import java.util.List;

public abstract class AbstractMiraklShopToSellerModelConverter implements Strategy<MiraklShop, SellerModel> {

	private final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor;

	private final SellersMiraklApiConfig sellersMiraklApiConfig;

	private final LanguageConverter languageConversion;

	protected AbstractMiraklShopToSellerModelConverter(
			final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
			final SellersMiraklApiConfig sellersMiraklApiConfig, final LanguageConverter languageConversion) {
		this.miraklShopBankAccountModelStrategyExecutor = miraklShopBankAccountModelStrategyExecutor;
		this.sellersMiraklApiConfig = sellersMiraklApiConfig;
		this.languageConversion = languageConversion;
	}

	protected SellerModel.SellerModelBuilder getCommonFieldsBuilder(final MiraklShop source) {
		final MiraklContactInformation contactInformation = source.getContactInformation();
		final List<MiraklAdditionalFieldValue> additionalFieldValues = source.getAdditionalFieldValues();
		final BankAccountModel bankAccountModel = miraklShopBankAccountModelStrategyExecutor.execute(source);
		final MiraklDefaultBillingInformation defaultBillingInformation = source.getDefaultBillingInformation();
		final String language = languageConversion.convert(defaultBillingInformation.getDefaultLanguage());
		//@formatter:off
		return SellerModel.builder()
				.language(language)
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
				.timeZone(sellersMiraklApiConfig.getTimeZone())
				.dateOfBirth(additionalFieldValues)
				.passportId(additionalFieldValues)
				.countryOfBirth(additionalFieldValues)
				.countryOfNationality(additionalFieldValues)
				.governmentId(additionalFieldValues)
				.governmentIdType(additionalFieldValues)
				.driversLicenseId(additionalFieldValues)
				.businessType(additionalFieldValues)
				.token(additionalFieldValues)
				.bankAccountDetails(bankAccountModel)
				.hwTermsConsent(additionalFieldValues)
				.hyperwalletProgram(additionalFieldValues);
		//@formatter:on
	}

}
