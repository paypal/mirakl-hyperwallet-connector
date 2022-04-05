package com.paypal.sellers.sellersextract.service;

import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;

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
