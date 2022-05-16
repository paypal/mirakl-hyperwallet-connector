package com.paypal.sellers.sellersextract.service.impl;

import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that creates the stakeholder in Hyperwallet
 */
@Slf4j
@Service
public class BusinessStakeholderExtractServiceImpl implements BusinessStakeholderExtractService {

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Override
	public List<BusinessStakeHolderModel> extractBusinessStakeHolders(
			final List<SellerModel> professionalSellerModels) {
		//@formatter:off
		return professionalSellerModels
				.stream()
				.map(SellerModel::getBusinessStakeHolderDetails)
				.flatMap(Collection::stream)
				.filter(Predicate.not(BusinessStakeHolderModel::isEmpty))
				.collect(Collectors.toList());
		//@formatter:on
	}

}
