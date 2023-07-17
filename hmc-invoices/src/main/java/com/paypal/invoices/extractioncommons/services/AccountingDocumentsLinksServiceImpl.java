package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.itemlinks.model.*;
import com.paypal.infrastructure.itemlinks.services.ItemLinksService;
import com.paypal.infrastructure.mirakl.support.MiraklShopUtils;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountingDocumentsLinksServiceImpl implements AccountingDocumentsLinksService {

	private static final Set<HyperwalletItemTypes> REQUIRED_HYPERWALLET_TYPES = Set
			.of(HyperwalletItemTypes.BANK_ACCOUNT, HyperwalletItemTypes.PROGRAM);

	private final ItemLinksService itemLinksService;

	private final MiraklInvoiceLinksService miraklInvoiceLinksService;

	public AccountingDocumentsLinksServiceImpl(final ItemLinksService itemLinksService,
			final MiraklInvoiceLinksService miraklInvoiceLinksService) {
		this.itemLinksService = itemLinksService;
		this.miraklInvoiceLinksService = miraklInvoiceLinksService;
	}

	@Override
	public <T extends AccountingDocumentModel> void storeRequiredLinks(final Collection<T> accountingDocumentModels) {
		final Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> retrievedFromHmcShopLinks = itemLinksService
				.findLinks(getMiraklItemLocators(getShopIdsSet(accountingDocumentModels)), REQUIRED_HYPERWALLET_TYPES);

		//@formatter:off
		final Set<MiraklItemLinkLocator> notFoundShopLinks = retrievedFromHmcShopLinks.entrySet().stream()
				.filter(e -> !hasAllRequiredShopLinks(e.getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		//@formatter:on

		final Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> retrievedFromMiraklShopLinks = miraklInvoiceLinksService
				.getInvoiceRelatedShopLinks(getShopIdsSet(notFoundShopLinks));

		retrievedFromMiraklShopLinks.forEach(itemLinksService::createLinks);
	}

	@Override
	public <T extends AccountingDocumentModel> Collection<HyperwalletItemLinkLocator> findRequiredLinks(
			final T accountingDocumentModel) {
		return itemLinksService.findLinks(
				new MiraklItemLinkLocator(accountingDocumentModel.getShopId(), MiraklItemTypes.SHOP),
				REQUIRED_HYPERWALLET_TYPES);
	}

	@Override
	public void updateLinksFromShops(@NotNull final Collection<MiraklShop> shops) {
		final Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> linksFromShops = shops.stream()
				.map(this::getItemLocatorsFromShop).filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		linksFromShops.forEach(itemLinksService::createLinks);
	}

	private Optional<Map.Entry<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>>> getItemLocatorsFromShop(
			final MiraklShop shop) {
		final MiraklItemLinkLocator miraklItemLinkLocator = createMiraklItemLocator(shop.getId());

		final Optional<HyperwalletItemLinkLocator> bankAccountItemLinkLocator = MiraklShopUtils
				.getBankAccountToken(shop).map(bankAccountToken -> new HyperwalletItemLinkLocator(bankAccountToken,
						HyperwalletItemTypes.BANK_ACCOUNT));
		final Optional<HyperwalletItemLinkLocator> programItemLinkLocator = MiraklShopUtils.getProgram(shop)
				.map(programToken -> new HyperwalletItemLinkLocator(programToken, HyperwalletItemTypes.PROGRAM));

		final Collection<HyperwalletItemLinkLocator> hyperwalletItemLinkLocators = Stream
				.of(bankAccountItemLinkLocator, programItemLinkLocator).filter(Optional::isPresent).map(Optional::get)
				.toList();

		return hyperwalletItemLinkLocators.isEmpty() ? Optional.empty()
				: Optional.of(Map.entry(miraklItemLinkLocator, hyperwalletItemLinkLocators));
	}

	private boolean hasAllRequiredShopLinks(final Collection<HyperwalletItemLinkLocator> hyperwalletItemLinkLocators) {
		if (hyperwalletItemLinkLocators.isEmpty()) {
			return false;
		}
		return hyperwalletItemLinkLocators.stream().map(HyperwalletItemLinkLocator::getType).collect(Collectors.toSet())
				.containsAll(REQUIRED_HYPERWALLET_TYPES);
	}

	@NotNull
	private <T extends AccountingDocumentModel> Set<String> getShopIdsSet(
			final Collection<T> accountingDocumentModels) {
		//@formatter:off
		return accountingDocumentModels.stream()
				.map(AccountingDocumentModel::getShopId)
				.collect(Collectors.toSet());
		//@formatter:on
	}

	@NotNull
	private Set<String> getShopIdsSet(final Set<MiraklItemLinkLocator> notFoundShopLinks) {
		return notFoundShopLinks.stream().map(ItemLinkLocator::getId).collect(Collectors.toSet());
	}

	@NotNull
	private Set<MiraklItemLinkLocator> getMiraklItemLocators(final Set<String> invoiceShopIds) {
		return invoiceShopIds.stream().map(this::createMiraklItemLocator).collect(Collectors.toSet());
	}

	private MiraklItemLinkLocator createMiraklItemLocator(final String shopId) {
		return new MiraklItemLinkLocator(shopId, MiraklItemTypes.SHOP);
	}

}
