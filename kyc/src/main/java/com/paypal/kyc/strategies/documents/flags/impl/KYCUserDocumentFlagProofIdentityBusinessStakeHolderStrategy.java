package com.paypal.kyc.strategies.documents.flags.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import com.paypal.kyc.strategies.documents.flags.AbstractUserDocumentFlagsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile({ "prod" })
public class KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategy extends AbstractUserDocumentFlagsStrategy {

	protected final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService;

	protected final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	protected KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategy(
			final MailNotificationUtil mailNotificationUtil,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient,
			final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService,
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService) {
		super(mailNotificationUtil, miraklMarketplacePlatformOperatorApiClient);
		this.hyperwalletBusinessStakeholderExtractService = hyperwalletBusinessStakeholderExtractService;
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void execute(final KYCUserDocumentFlagsNotificationBodyModel source) {

		final List<String> businessStakeholdersPendingToBeVerified = hyperwalletBusinessStakeholderExtractService
				.getKYCRequiredVerificationBusinessStakeHolders(source.getHyperwalletProgram(), source.getUserToken());

		final List<String> kycCustomValuesRequiredVerificationBusinessStakeholders = miraklBusinessStakeholderDocumentsExtractService
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(source.getClientUserId(),
						businessStakeholdersPendingToBeVerified);

		fillMiraklProofIdentityOrBusinessFlagStatus(source, kycCustomValuesRequiredVerificationBusinessStakeholders);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return HyperwalletUser.ProfileType.BUSINESS.equals(source.getProfileType())
				&& HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED
						.equals(source.getBusinessStakeholderVerificationStatus());
	}

	protected void fillMiraklProofIdentityOrBusinessFlagStatus(final KYCUserDocumentFlagsNotificationBodyModel source,
			final List<String> kycCustomValuesRequiredVerificationBusinessStakeholders) {
		if (CollectionUtils.isNotEmpty(kycCustomValuesRequiredVerificationBusinessStakeholders)) {
			final MiraklUpdateShop updateShop = new MiraklUpdateShop();

			final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = kycCustomValuesRequiredVerificationBusinessStakeholders
					.stream()
					.map(kycCustomValueRequiredVerification -> new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
							kycCustomValueRequiredVerification, Boolean.TRUE.toString()))
					.collect(Collectors.toList());

			updateShop.setShopId(Long.valueOf(source.getClientUserId()));
			updateShop.setAdditionalFieldValues(additionalFieldValues);

			try {
				log.info("Updating KYC proof of identity flag in Mirakl for business Stakeholder for shopId [{}]",
						source.getClientUserId());
				final MiraklUpdateShopsRequest miraklUpdateShopsRequest = new MiraklUpdateShopsRequest(
						List.of(updateShop));
				miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
				log.info("Proof of identity flag updated for business Stakeholder for shopId [{}]",
						source.getClientUserId());
			}
			catch (final MiraklException ex) {
				log.error(String.format(
						"Something went wrong updating KYC business stakeholder information of shop [%s]. Details [%s]",
						source.getClientUserId(), ex.getMessage()), ex);
				mailNotificationUtil.sendPlainTextEmail(
						"Issue detected updating KYC business stakeholder information in Mirakl",
						String.format(EMAIL_BODY_PREFIX
								+ "Something went wrong updating KYC business stakeholder information for shop [%s]%n%s",
								source.getClientUserId(), MiraklLoggingErrorsUtil.stringify(ex)));
				// Rethrow exception to handle it in AbstractNotificationListener
				throw ex;
			}
		}
	}

}
