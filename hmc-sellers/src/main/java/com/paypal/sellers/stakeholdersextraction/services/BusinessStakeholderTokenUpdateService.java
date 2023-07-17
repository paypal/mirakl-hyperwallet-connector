package com.paypal.sellers.stakeholdersextraction.services;

import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;

import java.util.List;

/**
 * Manipulates the interaction between the connector and Mirakl
 */
public interface BusinessStakeholderTokenUpdateService {

	/**
	 * Updates the token of every businessStakeholder belonging to an specific shop
	 * @param clientUserId the client user ID.
	 * @param businessStakeHolderModels a {@link List} of
	 * {@link BusinessStakeHolderModel}s.
	 */
	void updateBusinessStakeholderToken(String clientUserId, List<BusinessStakeHolderModel> businessStakeHolderModels);

}
