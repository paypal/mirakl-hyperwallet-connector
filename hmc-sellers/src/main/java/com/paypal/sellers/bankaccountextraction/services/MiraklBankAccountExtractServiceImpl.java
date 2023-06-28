package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.HYPERWALLET_BANK_ACCOUNT_TOKEN;

@Slf4j
@Service
public class MiraklBankAccountExtractServiceImpl implements MiraklBankAccountExtractService {

	private final MiraklClient miraklOperatorClient;

	private final MailNotificationUtil sellerMailNotificationUtil;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public MiraklBankAccountExtractServiceImpl(final MiraklClient miraklOperatorClient,
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
		log.info("Updating bank account token for shop [{}]", shopId);
		try {
			miraklOperatorClient.updateShops(request);
			log.info("Bank account token updated for shop [{}]", shopId);
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong updating information of shop [{}]", shopId);
			sellerMailNotificationUtil.sendPlainTextEmail("Issue detected updating bank token in Mirakl",
					(ERROR_MESSAGE_PREFIX + "Something went wrong updating bank token of shop [%s]%n%s")
							.formatted(shopId, MiraklLoggingErrorsUtil.stringify(ex)));
		}
	}

}
