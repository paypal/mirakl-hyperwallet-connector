package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentDownloadExtractService;
import com.paypal.kyc.strategies.documents.files.mirakl.impl.MiraklKYCSelectionDocumentExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MiraklBusinessStakeholderDocumentDownloadExtractService}
 */
@Slf4j
@Service
public class MiraklBusinessStakeholderDocumentDownloadExtractServiceImpl
		implements MiraklBusinessStakeholderDocumentDownloadExtractService {

	private final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	private final MiraklKYCSelectionDocumentExecutor miraklKYCSelectionDocumentStrategyExecutor;

	private final MailNotificationUtil kycMailNotificationUtil;

	public MiraklBusinessStakeholderDocumentDownloadExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient,
			final MiraklKYCSelectionDocumentExecutor miraklKYCSelectionDocumentStrategyExecutor,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
		this.miraklKYCSelectionDocumentStrategyExecutor = miraklKYCSelectionDocumentStrategyExecutor;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KYCDocumentBusinessStakeHolderInfoModel getBusinessStakeholderDocumentsSelectedBySeller(
			final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeHolderInfoModel) {

		final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeholderInfoModelWithMiraklShops = populateMiraklShopBusinessStakeholderDocuments(
				kycBusinessStakeHolderInfoModel);

		if (isLoARequiredForKYCButNotUploaded(kycBusinessStakeholderInfoModelWithMiraklShops)
				|| isLoARequiredButNotUploaded(kycBusinessStakeholderInfoModelWithMiraklShops)
				|| isDocumentMissingForKYC(kycBusinessStakeholderInfoModelWithMiraklShops)) {
			log.warn("Some needed documents are missing for shop [{}], skipping pushing all documents to hyperwallet",
					kycBusinessStakeholderInfoModelWithMiraklShops.getClientUserId());
			return kycBusinessStakeholderInfoModelWithMiraklShops;
		}

		//@formatter:off
		final List<KYCDocumentModel> extractedBusinessStakeholderDocumentsSelectedBySeller = miraklKYCSelectionDocumentStrategyExecutor
				.execute(kycBusinessStakeholderInfoModelWithMiraklShops)
				.stream()
				.flatMap(List::stream)
				.collect(Collectors.toList());
		//@formatter:on

		return kycBusinessStakeholderInfoModelWithMiraklShops.toBuilder()
				.documents(extractedBusinessStakeholderDocumentsSelectedBySeller).build();
	}

	private boolean isDocumentMissingForKYC(
			final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeholderInfoModelWithMiraklShops) {
		//@formatter:off
		return kycBusinessStakeholderInfoModelWithMiraklShops.isRequiresKYC()
				&& !kycBusinessStakeholderInfoModelWithMiraklShops.existsDocumentInMirakl();
		//@formatter:on
	}

	private boolean isLoARequiredButNotUploaded(
			final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeholderInfoModelWithMiraklShops) {
		//@formatter:off
		return kycBusinessStakeholderInfoModelWithMiraklShops.isRequiresLetterOfAuthorization()
				&& !kycBusinessStakeholderInfoModelWithMiraklShops.existsLetterOfAuthorizationDocumentInMirakl();
		//@formatter:on
	}

	private boolean isLoARequiredForKYCButNotUploaded(
			final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeholderInfoModelWithMiraklShops) {
		//@formatter:off
		// Requires KYC and LoA
		return kycBusinessStakeholderInfoModelWithMiraklShops.isRequiresLetterOfAuthorization() && kycBusinessStakeholderInfoModelWithMiraklShops.isRequiresKYC()
				// And LoA document is not uploaded to Mirakl
				&& (!kycBusinessStakeholderInfoModelWithMiraklShops.existsDocumentInMirakl() || !kycBusinessStakeholderInfoModelWithMiraklShops.existsLetterOfAuthorizationDocumentInMirakl());
		//@formatter:on
	}

	protected KYCDocumentBusinessStakeHolderInfoModel populateMiraklShopBusinessStakeholderDocuments(
			final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeHolderInfoModel) {
		final MiraklGetShopDocumentsRequest getShopBusinessStakeholderDocumentsRequest = new MiraklGetShopDocumentsRequest(
				List.of(kycBusinessStakeHolderInfoModel.getClientUserId()));

		try {
			log.info("Retrieving business stakeholder documents for seller with id [{}]",
					kycBusinessStakeHolderInfoModel.getClientUserId());
			final List<MiraklShopDocument> shopDocuments = miraklMarketplacePlatformOperatorApiClient
					.getShopDocuments(getShopBusinessStakeholderDocumentsRequest);

			//@formatter:off
			log.info("Business stakeholder documents available for seller with id [{}]: [{}]", kycBusinessStakeHolderInfoModel.getClientUserId(),
					shopDocuments.stream()
							.map(miraklDocument -> "Id:" + miraklDocument.getId() + " ,fileName:" + miraklDocument.getFileName() + " ,typeCode:" + miraklDocument.getTypeCode())
							.collect(Collectors.joining(" | ")));
			//@formatter:on
			return kycBusinessStakeHolderInfoModel.toBuilder().miraklShopDocuments(shopDocuments).build();
		}
		catch (final MiraklException e) {
			log.error(String.format(
					"Something went wrong trying to receive business stakeholder documents from Mirakl for seller with id [%s]",
					kycBusinessStakeHolderInfoModel.getClientUserId()), e);
			kycMailNotificationUtil.sendPlainTextEmail(
					"Issue detected getting business stakeholder documents from Mirakl",
					String.format("Something went wrong getting documents from Mirakl for shop Id [%s]%n%s",
							String.join(",", kycBusinessStakeHolderInfoModel.getClientUserId()),
							MiraklLoggingErrorsUtil.stringify(e)));
		}

		return kycBusinessStakeHolderInfoModel.toBuilder().build();
	}

}
