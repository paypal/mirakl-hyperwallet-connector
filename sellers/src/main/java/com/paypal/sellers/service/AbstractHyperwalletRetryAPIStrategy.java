package com.paypal.sellers.service;

import com.paypal.sellers.entity.AbstractFailedShopInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

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

	protected void executeRetryProcess(final String shopId, final Boolean includedAsFailed) {
		//@formatter:off
		Optional.of(includedAsFailed).filter(Boolean.FALSE::equals)
				.ifPresent(value -> failedEntityInformationService.deleteByShopId(shopId));

		Optional.of(includedAsFailed).filter(Boolean.TRUE::equals)
				.map(value -> failedEntityInformationService.findByShopId(shopId))
				.filter(CollectionUtils::isEmpty)
				.ifPresent(value -> failedEntityInformationService.save(shopId));
		//@formatter:on
	}

}
