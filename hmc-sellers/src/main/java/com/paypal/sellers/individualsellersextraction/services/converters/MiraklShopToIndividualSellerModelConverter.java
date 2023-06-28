package com.paypal.sellers.individualsellersextraction.services.converters;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellerextractioncommons.services.converters.AbstractMiraklShopToSellerModelConverter;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerProfileType;
import org.springframework.stereotype.Service;

@Service
public class MiraklShopToIndividualSellerModelConverter extends AbstractMiraklShopToSellerModelConverter {

	protected MiraklShopToIndividualSellerModelConverter(
			final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
			final SellersMiraklApiConfig sellersMiraklApiConfig) {
		super(miraklShopBankAccountModelStrategyExecutor, sellersMiraklApiConfig);
	}

	/**
	 * Method that retrieves a {@link MiraklShop} and returns a {@link SellerModel}
	 * @param source the source object {@link MiraklShop}
	 * @return the returned object {@link SellerModel}
	 */
	@Override
	public SellerModel execute(final MiraklShop source) {
		final var sellerModelBuilder = getCommonFieldsBuilder(source);
		sellerModelBuilder.profileType(SellerProfileType.INDIVIDUAL);

		return sellerModelBuilder.build();
	}

	@Override
	public boolean isApplicable(final MiraklShop source) {
		return !source.isProfessional();
	}

}
