package com.paypal.invoices.extractioncommons.aspects;

import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.paypal.invoices.extractioncommons.services.AccountingDocumentsLinksService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Aspect that updates the stored item links after the Mirakl shops are retrieved, so the
 * ItemLinks persistent cache is always up to date.
 */
@Slf4j
@Aspect
@Component
public class UpdateStoredItemLinksAspect {

	private final AccountingDocumentsLinksService accountingDocumentsLinksService;

	public UpdateStoredItemLinksAspect(final AccountingDocumentsLinksService accountingDocumentsLinksService) {
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
	}

	/**
	 * Intercepts the getShops method from the MiraklClient, and updates the stored item
	 * links after the shops are retrieved.
	 * @param pjp ProceedingJoinPoint
	 * @return the original MiraklShops returned by the intercepted method
	 * @throws Throwable if the intercepted method throws an exception
	 */
	@Around("execution(* com.paypal.infrastructure.mirakl.client.MiraklClient.getShops(..))")
	public MiraklShops interceptNotificationMethod(final ProceedingJoinPoint pjp) throws Throwable {
		final MiraklShops shops = (MiraklShops) pjp.proceed();
		updateStoredItemLinks(shops);

		return shops;
	}

	private void updateStoredItemLinks(final MiraklShops shops) {
		try {
			if (shops == null) {
				return;
			}

			accountingDocumentsLinksService
					.updateLinksFromShops(shops.getShops() != null ? shops.getShops() : List.of());
		}
		catch (final Exception e) {
			log.error("Error updating stored item links", e);
		}
	}

}
