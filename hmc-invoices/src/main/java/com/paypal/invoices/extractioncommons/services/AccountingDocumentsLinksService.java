package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * This service manages the relationships between Mirakl and Hyperwallet items that are
 * needed for the invoice creation process.
 *
 * It ensures that all the relationships required for the processing are stored in the HMC
 * and tries to recover from Mirakl the additional relationships that weren't found on the
 * HMC
 *
 * @see {@link MiraklInvoiceLinksService}
 */
public interface AccountingDocumentsLinksService {

	/**
	 * Stores the required relationships to be used during invoice processing for the
	 * collection of invoices provided.
	 *
	 * It checks if the relationships are already stored in HMC and if not found it tries
	 * to recovers the information from Mirakl using {@link MiraklInvoiceLinksService}
	 * @param accountingDocumentModels A collection of {@link AccountingDocumentModel}.
	 * @param <T> Type of the invoice
	 */
	<T extends AccountingDocumentModel> void storeRequiredLinks(Collection<T> accountingDocumentModels);

	/**
	 * Retrieves the required relationships between Mirakl and Hyperwallet needed for the
	 * processing of the provided invoice.
	 *
	 * These relationships are retrieved from HMC local storage, so not found
	 * relationships are not requested to Mirakl API.
	 * @param accountingDocumentModel The invoice.
	 * @param <T> Type of the invoice
	 * @return
	 */
	<T extends AccountingDocumentModel> Collection<HyperwalletItemLinkLocator> findRequiredLinks(
			T accountingDocumentModel);

	/**
	 * Updates the relationships between Mirakl and Hyperwallet for the provided shops.
	 * This information is used during the processing of the invoices
	 *
	 * This method is used to update the relationships when a shop is updated on Mirakl.
	 * @param shops A collection of {@link MiraklShop}.
	 */
	void updateLinksFromShops(@NotNull Collection<MiraklShop> shops);

}
