package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.sellers.bankaccountextract.service.MiraklBankAccountExtractService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.paypal.sellers.sellersextract.model.SellerModelConstants.HYPERWALLET_BANK_ACCOUNT_TOKEN;

@Slf4j
@Service
public class MiraklBankAccountExtractServiceImpl implements MiraklBankAccountExtractService {

	private final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	private final MailNotificationUtil sellerMailNotificationUtil;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public MiraklBankAccountExtractServiceImpl(final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
			final MailNotificationUtil sellerMailNotificationUtil) {
		this.miraklOperatorClient = miraklOperatorClient;
		this.sellerMailNotificationUtil = sellerMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateBankAccountToken(final SellerModel sellerModel,
			final HyperwalletBankAccount hyperwalletBankAccount) {
		final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
		final String shopId = sellerModel.getClientUserId();
		miraklUpdateShop.setShopId(Long.valueOf(shopId));
		final MiraklSimpleRequestAdditionalFieldValue userTokenCustomField = new MiraklSimpleRequestAdditionalFieldValue();
		userTokenCustomField.setCode(HYPERWALLET_BANK_ACCOUNT_TOKEN);
		userTokenCustomField.setValue(hyperwalletBankAccount.getToken());
		miraklUpdateShop.setAdditionalFieldValues(List.of(userTokenCustomField));
		final MiraklUpdateShopsRequest request = new MiraklUpdateShopsRequest(List.of(miraklUpdateShop));
		log.debug("Mirakl Update shop request: [{}]", ToStringBuilder.reflectionToString(request));
		log.info("Updating bank account token for shop [{}]", shopId);
		try {
			final MiraklUpdatedShops miraklUpdatedShops = miraklOperatorClient.updateShops(request);
			Optional.ofNullable(miraklUpdatedShops)
					.ifPresent(miraklUpdatedShopsResponse -> log.debug("Mirakl Update shop response: [{}]",
							ToStringBuilder.reflectionToString(miraklUpdatedShops)));

			log.info("Bank account token updated for shop [{}]", shopId);
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong updating information of shop [{}]", shopId);
			sellerMailNotificationUtil.sendPlainTextEmail("Issue detected updating bank token in Mirakl",
					String.format(ERROR_MESSAGE_PREFIX + "Something went wrong updating bank token of shop [%s]%n%s",
							shopId, MiraklLoggingErrorsUtil.stringify(ex)));
		}
	}

}
