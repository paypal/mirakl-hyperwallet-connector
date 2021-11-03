package com.paypal.sellers.sellersextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import com.paypal.sellers.sellersextract.service.SellersExtractService;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import com.paypal.sellers.service.FailedEntityInformationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that extracts the sellers and stakeholders from Mirakl and creates them in
 * Hyperwallet
 */
@Slf4j
@Service
public class SellersExtractServiceImpl implements SellersExtractService {

	private final MiraklSellersExtractService miraklSellersExtractService;

	private final FailedEntityInformationService<FailedSellersInformation> failedEntityInformationService;

	private final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor;

	private final BusinessStakeholderExtractService businessStakeHolderExtractService;

	public SellersExtractServiceImpl(final MiraklSellersExtractService miraklSellersExtractService,
			final FailedEntityInformationService<FailedSellersInformation> failedEntityInformationService,
			final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor,
			final BusinessStakeholderExtractService businessStakeHolderExtractService) {
		this.miraklSellersExtractService = miraklSellersExtractService;
		this.failedEntityInformationService = failedEntityInformationService;
		this.hyperWalletUserServiceStrategyExecutor = hyperWalletUserServiceStrategyExecutor;
		this.businessStakeHolderExtractService = businessStakeHolderExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HyperwalletUser> extractIndividuals(final Date delta) {
		final List<SellerModel> allMiraklIndividualSellers = internalExtractSellers(delta,
				SellerProfileType.INDIVIDUAL);
		return createOrUpdateSellers(allMiraklIndividualSellers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HyperwalletUser> extractProfessionals(final Date delta) {
		final List<HyperwalletUser> createdOrUpdatedSellers = createOrUpdateProfessionals(delta);
		createOrUpdateBusinessStakeholders(delta);
		return createdOrUpdatedSellers;
	}

	@NotNull
	protected List<HyperwalletUser> createOrUpdateProfessionals(final Date delta) {
		final List<SellerModel> allMiraklProfessionalSellers = internalExtractSellers(delta,
				SellerProfileType.BUSINESS);
		return createOrUpdateSellers(allMiraklProfessionalSellers);
	}

	protected void createOrUpdateBusinessStakeholders(final Date delta) {
		final List<SellerModel> miraklProfessionalSellersAfterCreation = internalExtractSellers(delta,
				SellerProfileType.BUSINESS);
		businessStakeHolderExtractService.extractBusinessStakeHolders(miraklProfessionalSellersAfterCreation);
	}

	/**
	 * Extracts the {@link SellerModel} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletUser} successfully created in hyperwallet
	 * @return a {@link List} of {@link SellerModel}
	 */
	@Override
	public List<HyperwalletUser> extractSellers(final Date delta) {
		final List<SellerModel> allMiraklSellers = internalExtractSellers(delta, null);
		return createOrUpdateSellers(allMiraklSellers);
	}

	@NonNull
	private List<HyperwalletUser> createOrUpdateSellers(final List<SellerModel> allMiraklIndividualSellers) {
		return allMiraklIndividualSellers.stream().map(hyperWalletUserServiceStrategyExecutor::execute)
				.collect(Collectors.toList());
	}

	/**
	 * TODO: Pending to create strategies for this
	 */
	@NonNull
	private List<SellerModel> internalExtractSellers(final Date delta, final SellerProfileType sellerProfileType) {
		final List<FailedSellersInformation> failedSellers = failedEntityInformationService.getAll();
		final List<SellerModel> allMiraklSellers = new ArrayList<>();
		if (SellerProfileType.BUSINESS.equals(sellerProfileType)) {
			final List<SellerModel> miraklProfessionalFailedSellers = miraklSellersExtractService.extractProfessionals(
					failedSellers.stream().map(FailedSellersInformation::getShopId).collect(Collectors.toList()));
			allMiraklSellers.addAll(miraklSellersExtractService.extractProfessionals(delta));
			allMiraklSellers.addAll(miraklProfessionalFailedSellers);
		}
		else if (SellerProfileType.INDIVIDUAL.equals(sellerProfileType)) {
			final List<SellerModel> miraklIndividualFailedSellers = miraklSellersExtractService.extractIndividuals(
					failedSellers.stream().map(FailedSellersInformation::getShopId).collect(Collectors.toList()));
			allMiraklSellers.addAll(miraklSellersExtractService.extractIndividuals(delta));
			allMiraklSellers.addAll(miraklIndividualFailedSellers);
		}
		else {
			final List<SellerModel> miraklFailedSellers = miraklSellersExtractService.extractSellers(
					failedSellers.stream().map(FailedSellersInformation::getShopId).collect(Collectors.toList()));
			allMiraklSellers.addAll(miraklSellersExtractService.extractSellers(delta));
			allMiraklSellers.addAll(miraklFailedSellers);
		}
		return allMiraklSellers;
	}

}
