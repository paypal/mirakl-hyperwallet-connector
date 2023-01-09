package com.paypal.infrastructure.sdk.mirakl.impl;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MiraklMarketplacePlatformOperatorApiClientWrapperImpl extends MiraklMarketplacePlatformOperatorApiClient
		implements MiraklMarketplacePlatformOperatorApiWrapper {

	@Resource
	private UserHyperwalletApiConfig userHyperwalletApiConfig;

	/**
	 * @param config {@link MiraklApiClientConfig} bean.
	 */
	public MiraklMarketplacePlatformOperatorApiClientWrapperImpl(final MiraklApiClientConfig config) {
		super(config.getEnvironment(), new MiraklCredential(config.getOperatorApiKey()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HMCMiraklInvoices getInvoices(final MiraklGetInvoicesRequest request) {
		return getHmcMiraklInvoices(request);
	}

	@Override
	public MiraklShops getShops(final MiraklGetShopsRequest request) {
		final MiraklShops shops = super.getShops(request);
		final List<MiraklShop> validShops = shops.getShops().stream().filter(Predicate.not(this::isIgnored))
				.collect(Collectors.toList());
		shops.setShops(validShops);
		shops.setTotalCount((long) validShops.size());

		return shops;
	}

	private boolean isIgnored(final MiraklShop miraklShop) {
		final Optional<String> program = getProgram(miraklShop);

		if (program.isPresent()) {
			final String programValue = program.get();
			final boolean isIgnored = userHyperwalletApiConfig.getIgnoredHyperwalletPrograms().contains(programValue);
			if (isIgnored) {
				log.info("Shop with id [{}] contains program [{}] which is in the ignored list, skipping processing",
						miraklShop.getId(), programValue);
			}
			return isIgnored;
		}
		else {
			log.debug("Program not set for shop with id [{}]", miraklShop.getId());
			return true;
		}
	}

	private static Optional<String> getProgram(final MiraklShop miraklShop) {
		return miraklShop.getAdditionalFieldValues().stream().filter(field -> field.getCode().equals("hw-program"))
				.filter(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::isInstance)
				.map(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::cast).findAny()
				.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
	}

	protected HMCMiraklInvoices getHmcMiraklInvoices(final MiraklGetInvoicesRequest request) {
		return get(request, HMCMiraklInvoices.class);
	}

}
