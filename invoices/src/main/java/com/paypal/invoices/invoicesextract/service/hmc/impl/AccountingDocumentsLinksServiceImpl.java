package com.paypal.invoices.invoicesextract.service.hmc.impl;

import com.paypal.infrastructure.itemlinks.model.*;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklInvoiceLinksService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountingDocumentsLinksServiceImpl implements AccountingDocumentsLinksService {

	private static final Set<HyperwalletItemTypes> REQUIRED_HYPERWALLET_TYPES = Set
			.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM);

	private final ItemLinksService itemLinksService;

	private final MiraklInvoiceLinksService miraklInvoiceLinksService;

	public AccountingDocumentsLinksServiceImpl(ItemLinksService itemLinksService,
			MiraklInvoiceLinksService miraklInvoiceLinksService) {
		this.itemLinksService = itemLinksService;
		this.miraklInvoiceLinksService = miraklInvoiceLinksService;
	}

	@Override
	public <T extends AccountingDocumentModel> void storeRequiredLinks(Collection<T> accountingDocumentModels) {
		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> retrievedFromHmcShopLinks = itemLinksService
				.findLinks(getMiraklItemLocators(getShopIdsSet(accountingDocumentModels)), REQUIRED_HYPERWALLET_TYPES);

		//@formatter:off
		Set<MiraklItemLinkLocator> notFoundShopLinks = retrievedFromHmcShopLinks.entrySet().stream()
				.filter(e -> !hasAllRequiredShopLinks(e.getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		//@formatter:on

		Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> retrievedFromMiraklShopLinks = miraklInvoiceLinksService
				.getInvoiceRelatedShopLinks(getShopIdsSet(notFoundShopLinks));

		retrievedFromMiraklShopLinks.forEach(itemLinksService::createLinks);
	}

	@Override
	public <T extends AccountingDocumentModel> Collection<HyperwalletItemLinkLocator> findRequiredLinks(
			T accountingDocumentModel) {
		return itemLinksService.findLinks(
				new MiraklItemLinkLocator(accountingDocumentModel.getShopId(), MiraklItemTypes.SHOP),
				REQUIRED_HYPERWALLET_TYPES);
	}

	private boolean hasAllRequiredShopLinks(Collection<HyperwalletItemLinkLocator> hyperwalletItemLinkLocators) {
		if (hyperwalletItemLinkLocators.isEmpty()) {
			return false;
		}
		return hyperwalletItemLinkLocators.stream().map(HyperwalletItemLinkLocator::getType).collect(Collectors.toSet())
				.containsAll(REQUIRED_HYPERWALLET_TYPES);
	}

	@NotNull
	private <T extends AccountingDocumentModel> Set<String> getShopIdsSet(Collection<T> accountingDocumentModels) {
		//@formatter:off
		return accountingDocumentModels.stream()
				.map(AccountingDocumentModel::getShopId)
				.collect(Collectors.toSet());
		//@formatter:on
	}

	@NotNull
	private Set<String> getShopIdsSet(Set<MiraklItemLinkLocator> notFoundShopLinks) {
		return notFoundShopLinks.stream().map(ItemLinkLocator::getId).collect(Collectors.toSet());
	}

	@NotNull
	private Set<MiraklItemLinkLocator> getMiraklItemLocators(Set<String> invoiceShopIds) {
		return invoiceShopIds.stream().map(this::createMiraklItemLocator).collect(Collectors.toSet());
	}

	private MiraklItemLinkLocator createMiraklItemLocator(String shopId) {
		return new MiraklItemLinkLocator(shopId, MiraklItemTypes.SHOP);
	}

}
