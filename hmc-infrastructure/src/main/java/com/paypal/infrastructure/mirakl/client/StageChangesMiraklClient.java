package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.paypal.infrastructure.changestaging.service.ChangeStagingService;
import com.paypal.infrastructure.mirakl.client.converters.MiraklStageChangeConverter;
import com.paypal.infrastructure.mirakl.client.filter.IgnoredShopsFilter;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import com.paypal.infrastructure.mirakl.settings.MiraklClientSettingsHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class StageChangesMiraklClient extends DirectMiraklClient {

	private final ChangeStagingService changeStagingService;

	private final MiraklStageChangeConverter miraklStageChangeConverter;

	public StageChangesMiraklClient(final MiraklApiClientConfig config, final IgnoredShopsFilter ignoredShopsFilter,
			final ChangeStagingService changeStagingService,
			final MiraklStageChangeConverter miraklStageChangeConverter) {
		super(config, ignoredShopsFilter);
		this.changeStagingService = changeStagingService;
		this.miraklStageChangeConverter = miraklStageChangeConverter;
	}

	@Override
	public MiraklUpdatedShops updateShops(final MiraklUpdateShopsRequest miraklUpdateShopsRequest) {
		if (MiraklClientSettingsHolder.getMiraklClientSettings().isStageChanges()) {
			changeStagingService.stageChanges(miraklStageChangeConverter.from(miraklUpdateShopsRequest.getShops()));

			// This updates will be made asynchronously in other thread after batching
			// some changes, so we return an empty response.
			// If the return response is needed, staging can't be used.
			final MiraklUpdatedShops miraklUpdatedShops = new MiraklUpdatedShops();
			miraklUpdatedShops.setShopReturns(List.of());
			return miraklUpdatedShops;
		}
		else {
			return callSuperGetMiraklUpdatedShops(miraklUpdateShopsRequest);
		}
	}

	// For testing purposes
	protected MiraklUpdatedShops callSuperGetMiraklUpdatedShops(
			final MiraklUpdateShopsRequest miraklUpdateShopsRequest) {
		return super.updateShops(miraklUpdateShopsRequest);
	}

}
