package com.paypal.sellers.service;

import com.paypal.sellers.entity.AbstractFailedShopInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Abstract class that saves or deletes id shops in retry system
 *
 * @param <T> extends {@link AbstractFailedShopInformation}
 */
@Slf4j
public abstract class AbstractHyperwalletRetryAPIStrategy<T extends AbstractFailedShopInformation> {

	protected final FailedEntityInformationService<T> failedEntityInformationService;

	protected AbstractHyperwalletRetryAPIStrategy(
			final FailedEntityInformationService<T> failedEntityInformationService) {
		this.failedEntityInformationService = failedEntityInformationService;
	}

	protected void executeRetryProcess(final String shopId, final boolean includedAsFailed) {
		if (includedAsFailed) {
			final List<T> entityInformation = failedEntityInformationService.findByShopId(shopId);
			if (CollectionUtils.isEmpty(entityInformation)) {
				failedEntityInformationService.save(shopId);
			}
		}
		else {
			failedEntityInformationService.deleteByShopId(shopId);
		}
	}

}
