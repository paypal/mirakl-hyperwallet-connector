package com.paypal.sellers.sellersextract.service;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Service that orchestrates the flow to extract sellers from mirakl and create them as
 * users into hyperwallet
 */
public interface SellersExtractService {

	/**
	 * Extracts the {@link SellerModel} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletUser} successfully created in hyperwallet
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<HyperwalletUser> extractIndividuals(@Nullable Date delta);

	/**
	 * Extracts the {@link SellerModel} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletUser} successfully created in hyperwallet
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<HyperwalletUser> extractProfessionals(@Nullable Date delta);

	/**
	 * Extracts the {@link SellerModel} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletUser} successfully created in hyperwallet
	 * @return a {@link List} of {@link SellerModel}
	 */
	List<HyperwalletUser> extractSellers(@Nullable Date delta);

}
