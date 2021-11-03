package com.paypal.sellers.sellersextract.service.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.sellers.infrastructure.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Class to help testing concurrent executions of
 * {@link MiraklSellersExtractService#extractIndividuals(java.util.Date)}
 */
@Profile({ "qa" })
@Slf4j
@Service("miraklExtractSellerService")
public class TestingMiraklSellersExtractServiceImpl extends MiraklSellersExtractServiceImpl
		implements MiraklSellersExtractService {

	private final SellersMiraklApiConfig sellersMiraklApiConfig;

	public TestingMiraklSellersExtractServiceImpl(final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
			final StrategyExecutor<MiraklShop, SellerModel> miraklShopSellerModelStrategyExecutor,
			final SellersMiraklApiConfig sellersMiraklApiConfig, final MailNotificationUtil mailNotificationUtil) {
		super(miraklOperatorClient, miraklShopSellerModelStrategyExecutor, mailNotificationUtil);
		this.sellersMiraklApiConfig = sellersMiraklApiConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractIndividuals(final Date delta) {
		delay();
		return callSuperExtractIndividuals(delta);
	}

	@Override
	public List<SellerModel> extractProfessionals(final Date delta) {
		delay();
		return callSuperExtractProfessionals(delta);
	}

	private void delay() {
		try {
			Thread.sleep(sellersMiraklApiConfig.getTestingDelay());
		}
		catch (final InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	protected List<SellerModel> callSuperExtractIndividuals(final Date delta) {
		return super.extractIndividuals(delta);
	}

	protected List<SellerModel> callSuperExtractProfessionals(final Date delta) {
		return super.extractProfessionals(delta);
	}

}
