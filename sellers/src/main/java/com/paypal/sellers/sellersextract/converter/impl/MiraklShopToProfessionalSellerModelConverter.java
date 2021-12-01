package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.infrastructure.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 * Strategy to ensure the converts only happens when the user is a Professional type
 */
@Service
public class MiraklShopToProfessionalSellerModelConverter extends AbstractMiraklShopToSellerModelConverter {

	private final Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> pairBusinessStakeHolderModelConverter;

	protected MiraklShopToProfessionalSellerModelConverter(
			final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
			final Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> pairBusinessStakeHolderModelConverter,
			final SellersMiraklApiConfig sellersMiraklApiConfig) {
		super(miraklShopBankAccountModelStrategyExecutor, sellersMiraklApiConfig);
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

		final List<MiraklAdditionalFieldValue> additionalFieldValues = source.getAdditionalFieldValues();

		return sellerModelBuilder.profileType(SellerProfileType.BUSINESS)
				.companyRegistrationCountry(additionalFieldValues)
				.businessRegistrationStateProvince(additionalFieldValues)
				.companyName(source.getProfessionalInformation().getCorporateName())
				.companyRegistrationNumber(source.getProfessionalInformation().getIdentificationNumber())
				.vatNumber(source.getProfessionalInformation().getTaxIdentificationNumber())
				.businessStakeHolderDetails(businessStakeHolderList)
				.build();
		//@formatter:on
	}

	@Override
	public boolean isApplicable(final MiraklShop source) {
		return source.isProfessional();
	}

}
