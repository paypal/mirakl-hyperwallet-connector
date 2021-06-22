package com.paypal.sellers.sellersextract.service;

import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;

import java.util.List;

/**
 * Manipulates the interaction between the connector and Mirakl
 */
public interface MiraklBusinessStakeholderExtractService {

	/**
	 * Updates the token of every businessStakeholder belonging to an specific shop
	 * @param clientUserId
	 * @param businessStakeHolderModels
	 */
	void updateBusinessStakeholderToken(String clientUserId, List<BusinessStakeHolderModel> businessStakeHolderModels);

}
