package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemTypes;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MiraklInvoiceLinksServiceImpl implements MiraklInvoiceLinksService {

	private final MiraklClient miraklMarketplacePlatformOperatorApiClient;

	private final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter;

	public MiraklInvoiceLinksServiceImpl(final MiraklClient miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter) {
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
		this.miraklShopToAccountingModelConverter = miraklShopToAccountingModelConverter;
	}

	@Override
	public Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> getInvoiceRelatedShopLinks(
			final Set<String> shopIds) {
		if (shopIds.isEmpty()) {
			return Map.of();
		}

		final Map<String, MiraklShop> miraklShops = getAllShops(shopIds);

		//@formatter:off
		return shopIds.stream()
				.map(this::createMiraklItemLocator)
				.collect(Collectors.toMap(Function.identity(), miraklLocator -> getHyperwalletLinks(miraklShops.get(miraklLocator.getId()))));
		//@formatter:on
	}

	private Collection<HyperwalletItemLinkLocator> getHyperwalletLinks(final MiraklShop miraklShop) {
		if (miraklShop == null) {
			return Collections.emptyList();
		}
		return extractHyperwalletItemLocators(miraklShopToAccountingModelConverter.convert(miraklShop));
	}

	private Set<HyperwalletItemLinkLocator> extractHyperwalletItemLocators(final AccountingDocumentModel invoiceModel) {
		final Set<HyperwalletItemLinkLocator> hyperwalletItemLinkLocators = new HashSet<>();

		if (StringUtils.isNotEmpty(invoiceModel.getDestinationToken())) {
			hyperwalletItemLinkLocators.add(new HyperwalletItemLinkLocator(invoiceModel.getDestinationToken(),
					HyperwalletItemTypes.BANK_ACCOUNT));
		}
		if (StringUtils.isNotEmpty(invoiceModel.getHyperwalletProgram())) {
			hyperwalletItemLinkLocators.add(
					new HyperwalletItemLinkLocator(invoiceModel.getHyperwalletProgram(), HyperwalletItemTypes.PROGRAM));
		}

		return hyperwalletItemLinkLocators;
	}

	private MiraklItemLinkLocator createMiraklItemLocator(final String shopId) {
		return new MiraklItemLinkLocator(shopId, MiraklItemTypes.SHOP);
	}

	protected Map<String, MiraklShop> getAllShops(final Set<String> shopIds) {
		final List<List<String>> partitionedShopIds = ListUtils.partition(new ArrayList<>(shopIds), 100);
		//@formatter:off
		final List<MiraklShop> miraklShops = partitionedShopIds.stream()
				.map(this::getAllShopsWithoutPartitioning)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		//@formatter:on

		return miraklShops.stream().collect(Collectors.toMap(MiraklShop::getId, Function.identity()));
	}

	private List<MiraklShop> getAllShopsWithoutPartitioning(final List<String> shopIds) {
		if (shopIds.isEmpty()) {
			return Collections.emptyList();
		}

		try {
			final MiraklGetShopsRequest request = createShopRequest(new HashSet<>(shopIds));
			final MiraklShops miraklShops = miraklMarketplacePlatformOperatorApiClient.getShops(request);
			return miraklShops.getShops();
		}
		catch (final MiraklApiException ex) {
			log.warn(
					"Error while recovering shop info during invoice processing. Failing shop ids[%s]. Failure reason: %s"
							.formatted(String.join(",", shopIds), MiraklLoggingErrorsUtil.stringify(ex)));
			return Collections.emptyList();
		}
	}

	@NonNull
	private MiraklGetShopsRequest createShopRequest(final Set<String> shopIds) {
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		request.setShopIds(shopIds);
		request.setPaginate(false);
		return request;
	}

}
