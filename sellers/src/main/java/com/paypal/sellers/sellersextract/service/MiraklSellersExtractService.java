package com.paypal.sellers.sellersextract.service;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Service to manipulate the shop extraction from Mirakl
 */
public interface MiraklSellersExtractService {

	/**
	 * Extracts the {@link SellerModel} individuals data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<SellerModel> extractIndividuals(@Nullable Date delta);

	/**
	 * Extracts the {@link SellerModel} professionals data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<SellerModel> extractProfessionals(@Nullable Date delta);

	/**
	 * Extracts all {@link SellerModel} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<SellerModel> extractSellers(@Nullable Date delta);

	/**
	 * Extracts all {@link SellerModel} individuals data from Mirakl environment
	 * @param shopIds {@link List<String>} that includes all shop ids to retrieve from
	 * database
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<SellerModel> extractIndividuals(List<String> shopIds);

	/**
	 * Extracts all {@link SellerModel} professionals data from Mirakl environment
	 * @param shopIds {@link List<String>} that includes all shop ids to retrieve from
	 * database
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<SellerModel> extractProfessionals(List<String> shopIds);

	/**
	 * Extracts all {@link SellerModel} data from Mirakl environment
	 * @param shopIds {@link List<String>} that includes all shop ids to retrieve from
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<SellerModel> extractSellers(List<String> shopIds);

	/**
	 * Updates the custom field {@code hw-user-field} with the token received after
	 * creation of the {@link HyperwalletUser}
	 * @param hyperwalletUser
	 */
	void updateUserToken(HyperwalletUser hyperwalletUser);

}
