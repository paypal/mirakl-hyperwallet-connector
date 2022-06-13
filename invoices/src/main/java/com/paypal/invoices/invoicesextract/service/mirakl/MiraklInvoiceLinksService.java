package com.paypal.invoices.invoicesextract.service.mirakl;

import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This service retrieves from Mirakl the relationships with Hyperwallet items that are
 * required for invoice processing. This relationships are:
 * <ul>
 * <li>{@link com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes#BANK_ACCOUNT}</li>
 * <li>{@link com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes#PROGRAM}</li>
 * </ul>
 *
 * This relationships are obtained from the custom fields of the Mirakl shop, so S20 will
 * be used to retrieve this information.
 */
public interface MiraklInvoiceLinksService {

	/**
	 * Retrieves from Mirakl the relationships with Hyperwallet items required for invoice
	 * processing for the provided shop ids.
	 * @param shopIds List of shop ids.
	 * @return A Map with the references to a Mirakl item of type Shop and the list of
	 * related Hyperwallet items references.
	 */
	Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> getInvoiceRelatedShopLinks(Set<String> shopIds);

}
