package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.strategy.StrategyFactory;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import org.springframework.stereotype.Service;

@Service
public class MiraklShopToIndividualSellerModelConverter extends AbstractMiraklShopToSellerModelConverter {

	protected MiraklShopToIndividualSellerModelConverter(
			final StrategyFactory<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyFactory) {
		super(miraklShopBankAccountModelStrategyFactory);
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
