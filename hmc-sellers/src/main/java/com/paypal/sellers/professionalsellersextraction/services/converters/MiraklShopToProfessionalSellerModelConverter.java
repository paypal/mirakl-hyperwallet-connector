package com.paypal.sellers.professionalsellersextraction.services.converters;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerProfileType;
import com.paypal.sellers.sellerextractioncommons.services.converters.AbstractMiraklShopToSellerModelConverter;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.utils.LanguageConverter;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 * Strategy to ensure the converts only happens when the user is a Professional type
 */
@Service
public class MiraklShopToProfessionalSellerModelConverter extends AbstractMiraklShopToSellerModelConverter {

	private final Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> pairBusinessStakeHolderModelConverter;

	@Value("#{'${hmc.hyperwallet.countries.not.local.tax}'.split(',')}")
	private List<String> countriesNotLocalTax;

	protected MiraklShopToProfessionalSellerModelConverter(
			final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
			final Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> pairBusinessStakeHolderModelConverter,
			final SellersMiraklApiConfig sellersMiraklApiConfig, final LanguageConverter languageConversion) {
		super(miraklShopBankAccountModelStrategyExecutor, sellersMiraklApiConfig, languageConversion);
		this.pairBusinessStakeHolderModelConverter = pairBusinessStakeHolderModelConverter;
	}

	/**
	 * Method that retrieves a {@link MiraklShop} and returns a {@link SellerModel}
	 * @param source the source object {@link MiraklShop}
	 * @return the returned object {@link SellerModel}
	 */
	@Override
	public SellerModel execute(final MiraklShop source) {
		final var sellerModelBuilder = getCommonFieldsBuilder(source);
//@formatter:off
		final List<BusinessStakeHolderModel> businessStakeHolderList = IntStream.range(1, 6).mapToObj(
						i -> pairBusinessStakeHolderModelConverter.convert(Triple.of(source.getAdditionalFieldValues(), i, source.getId())))
				.filter(Objects::nonNull)
				.filter(Predicate.not(BusinessStakeHolderModel::isEmpty))
				.collect(Collectors.toCollection(ArrayList::new));

		final MiraklDefaultBillingInformation.CorporateInformation corporateInformation = Optional.of(source)
				.map(MiraklShop::getDefaultBillingInformation)
				.map(MiraklDefaultBillingInformation::getCorporateInformation)
				.orElse(null);

		final MiraklDefaultBillingInformation.FiscalInformation fiscalInformation = Optional.of(source)
				.map(MiraklShop::getDefaultBillingInformation)
				.map(MiraklDefaultBillingInformation::getFiscalInformation)
				.orElse(null);

		final List<MiraklAdditionalFieldValue> additionalFieldValues = source.getAdditionalFieldValues();
		return sellerModelBuilder.profileType(SellerProfileType.BUSINESS)
				.companyRegistrationCountry(additionalFieldValues)
				.businessRegistrationStateProvince(additionalFieldValues)
				.companyName(corporateInformation != null ? corporateInformation.getCompanyRegistrationName() : null)
				.companyRegistrationNumber(corporateInformation != null ? corporateInformation.getCompanyRegistrationNumber() : null)
				.vatNumber(fiscalInformation != null ? getTaxNumber(fiscalInformation) : null)
				.businessStakeHolderDetails(businessStakeHolderList)
				.build();
		//@formatter:on
	}

	@Override
	public boolean isApplicable(final MiraklShop source) {
		return source.isProfessional();
	}

	/**
	 * Method that retrieves the tax number based on the country
	 * @param fiscalInformation the fiscal information
	 * @return the tax number
	 */
	protected String getTaxNumber(final MiraklDefaultBillingInformation.FiscalInformation fiscalInformation) {
		if (fiscalInformation.getTaxIdentificationCountry() == null
				|| fiscalInformation.getTaxIdentificationCountry().isBlank()) {
			return fiscalInformation.getLocalTaxNumber();
		}
		else if (countriesNotLocalTax.contains(fiscalInformation.getTaxIdentificationCountry())) {
			return fiscalInformation.getTaxIdentificationNumber();
		}
		else {
			return fiscalInformation.getLocalTaxNumber();
		}

	}

}
