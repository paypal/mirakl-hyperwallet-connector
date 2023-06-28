package com.paypal.infrastructure.itemlinks.services;

import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This service is used to keep track of the relationships between Mirakl and Hyperwallet
 * entities. This information in persisted in the database so is kept between restarts.
 *
 */
@Service
public interface ItemLinksService {

	/**
	 * Stores the provided links.
	 * @param miraklItemLocator A reference to a Mirakl item.
	 * @param hyperwalletItemLocators A collection of references to Hyperwallet items.
	 */
	void createLinks(MiraklItemLinkLocator miraklItemLocator,
			Collection<HyperwalletItemLinkLocator> hyperwalletItemLocators);

	/**
	 * Retrieves all the links of the requested types for the list of Mirakl references
	 * passed
	 * @param sourceItem A reference to a Mirakl item.
	 * @param targetTypes The types of Hyperwallet items to find.
	 * @return A map with a collection of Hyperwallet references for each of the requested
	 * Mirakl references.
	 */
	Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> findLinks(
			Collection<MiraklItemLinkLocator> sourceItem, Set<HyperwalletItemTypes> targetTypes);

	/**
	 * Retrieves all the links of the requested types for the list of Mirakl references
	 * passed
	 * @param sourceItem A reference to a Mirakl item.
	 * @param hyperwalletItemTypes The types of Hyperwallet items to find.
	 * @return all items relationships found.
	 */
	Collection<HyperwalletItemLinkLocator> findLinks(MiraklItemLinkLocator sourceItem,
			Set<HyperwalletItemTypes> hyperwalletItemTypes);

}
