package com.paypal.sellers.stakeholdersextraction.services;

import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;

import java.util.List;

/**
 * Holds the logic to manipulate stakeholders between mirakl and hyperwallet
 */
public interface BusinessStakeholderExtractService {

	/**
	 * Extracts the information held in Mirakl for the {@code professionalSellerModels}
	 * and the token stored in {@code createdHyperWalletUsers} to create Business
	 * Stakeholders in Hyperwallet and update the token in Mirakl
	 * @param professionalSellerModels
	 * @return
	 */
	List<BusinessStakeHolderModel> extractBusinessStakeHolders(List<SellerModel> professionalSellerModels);

}
