package com.paypal.kyc.strategies.documents.flags.impl;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.infrastructure.BusinessStakeholderTestHelper;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service("kycUserDocumentFlagProofIdentityBusinessStakeHolderStrategy")
@Profile({ "!prod" })
public class KYCUserDocumentFlagProofIdentityBusinessStakeHolderMockStrategy
		extends KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategy {

	private final BusinessStakeholderTestHelper businessStakeholderTestHelper;

	protected KYCUserDocumentFlagProofIdentityBusinessStakeHolderMockStrategy(
			final MailNotificationUtil mailNotificationUtil,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient,
			final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService,
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final BusinessStakeholderTestHelper businessStakeholderTestHelper) {
		super(mailNotificationUtil, miraklMarketplacePlatformOperatorApiClient,
				hyperwalletBusinessStakeholderExtractService, miraklBusinessStakeholderDocumentsExtractService);
		this.businessStakeholderTestHelper = businessStakeholderTestHelper;
	}

	@Override
	public Optional<Void> execute(final KYCUserDocumentFlagsNotificationBodyModel source) {
		final List<String> businessStakeholdersPendingToBeVerified = businessStakeholderTestHelper
				.getRequiresVerificationBstk(source.getClientUserId());

		log.info("Required Business stakeholders to be put as required kyc [{}]",
				String.join(",", businessStakeholdersPendingToBeVerified));

		final List<String> kycCustomValuesRequiredVerificationBusinessStakeholders = miraklBusinessStakeholderDocumentsExtractService
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(source.getClientUserId(),
						businessStakeholdersPendingToBeVerified);

		return fillMiraklProofIdentityOrBusinessFlagStatus(source,
				kycCustomValuesRequiredVerificationBusinessStakeholders);
	}

}
