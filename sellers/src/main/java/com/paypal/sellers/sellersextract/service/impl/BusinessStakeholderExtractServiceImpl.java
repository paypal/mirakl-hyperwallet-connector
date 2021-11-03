package com.paypal.sellers.sellersextract.service.impl;

import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletBusinessStakeHolderServiceStrategyExecutorExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that creates the stakeholder in Hyperwallet
 */
@Slf4j
@Service
public class BusinessStakeholderExtractServiceImpl implements BusinessStakeholderExtractService {

	private final HyperWalletBusinessStakeHolderServiceStrategyExecutorExecutor hyperWalletBusinessStakeHolderServiceStrategyExecutor;

	private final MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractService;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public BusinessStakeholderExtractServiceImpl(
			final HyperWalletBusinessStakeHolderServiceStrategyExecutorExecutor hyperWalletBusinessStakeHolderServiceStrategyExecutor,
			final MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractService) {
		this.hyperWalletBusinessStakeHolderServiceStrategyExecutor = hyperWalletBusinessStakeHolderServiceStrategyExecutor;
		this.miraklBusinessStakeholderExtractService = miraklBusinessStakeholderExtractService;
	}

	@Override
	public void extractBusinessStakeHolders(final List<SellerModel> professionalSellerModels) {
		//@formatter:off
		professionalSellerModels
				.stream()
				.map(SellerModel::getBusinessStakeHolderDetails)
				.flatMap(Collection::stream)
				.filter(Predicate.not(BusinessStakeHolderModel::isEmpty))
				.map(hyperWalletBusinessStakeHolderServiceStrategyExecutor::execute)
				.filter(Objects::nonNull)
				.filter(BusinessStakeHolderModel::isJustCreated)
				.collect(Collectors.groupingBy(BusinessStakeHolderModel::getClientUserId))
				.forEach(miraklBusinessStakeholderExtractService::updateBusinessStakeholderToken);
		//@formatter:on
	}

}
