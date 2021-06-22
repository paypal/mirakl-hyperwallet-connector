package com.paypal.sellers.bankaccountextract.service.impl;

import com.paypal.sellers.bankaccountextract.service.BankAccountExtractService;
import com.paypal.sellers.bankaccountextract.service.strategies.HyperWalletBankAccountServiceStrategyFactorySingle;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import com.paypal.sellers.service.FailedEntityInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BankAccountExtractService}
 */
@Slf4j
@Service
public class BankAccountExtractServiceImpl implements BankAccountExtractService {

	private final MiraklSellersExtractService miraklSellersExtractService;

	private final FailedEntityInformationService<FailedBankAccountInformation> failedEntityInformationService;

	private final HyperWalletBankAccountServiceStrategyFactorySingle hyperWalletBankAccountServiceStrategyFactory;

	public BankAccountExtractServiceImpl(final MiraklSellersExtractService miraklSellersExtractService,
			final FailedEntityInformationService<FailedBankAccountInformation> failedEntityInformationService,
			final HyperWalletBankAccountServiceStrategyFactorySingle hyperWalletBankAccountServiceStrategyFactory) {
		this.miraklSellersExtractService = miraklSellersExtractService;
		this.failedEntityInformationService = failedEntityInformationService;
		this.hyperWalletBankAccountServiceStrategyFactory = hyperWalletBankAccountServiceStrategyFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void extractBankAccounts(final Date delta) {
		final List<SellerModel> sellers = internalExtractSellers(delta);
		sellers.forEach(hyperWalletBankAccountServiceStrategyFactory::execute);
	}

	@NonNull
	private List<SellerModel> internalExtractSellers(final Date delta) {
		final List<FailedBankAccountInformation> failedSellers = failedEntityInformationService.getAll();
		final List<SellerModel> allMiraklSellers = new ArrayList<>();
		final List<SellerModel> miraklFailedSellers = miraklSellersExtractService.extractSellers(
				failedSellers.stream().map(FailedBankAccountInformation::getShopId).collect(Collectors.toList()));
		allMiraklSellers.addAll(miraklSellersExtractService.extractSellers(delta));
		allMiraklSellers.addAll(miraklFailedSellers);

		return allMiraklSellers;
	}

}
