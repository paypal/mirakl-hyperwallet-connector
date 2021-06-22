package com.paypal.sellers.service;

import com.paypal.sellers.entity.AbstractFailedShopInformation;
import com.paypal.sellers.entity.FailedSellersInformation;

import java.util.List;

/**
 * Service that manages database operations with {@link FailedSellersInformation}
 */
public interface FailedEntityInformationService<T extends AbstractFailedShopInformation> {

	/**
	 * Save {@link List<String>} shopIds into database
	 * @param shopIds
	 */
	void saveAll(List<String> shopIds);

	/**
	 * Save {@link String} shopId into database
	 * @param shopId
	 */
	void save(String shopId);

	/**
	 * Gets a list of {@link T}
	 * @return {@link List<T>}
	 */
	List<T> getAll();

	/**
	 * Deletes, if exists, the shopId received as parameter
	 * @param shopId
	 */
	void deleteByShopId(String shopId);

	/**
	 * Get, if exists, a {@link List<T>} of shopId objects
	 * @param shopId
	 * @return {@link List<T>}
	 */
	List<T> findByShopId(String shopId);

}
