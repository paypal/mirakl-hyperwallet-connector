package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentDownloadExtractService;
import com.paypal.kyc.strategies.documents.files.mirakl.impl.MiraklKYCSelectionDocumentExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MiraklSellerDocumentDownloadExtractService}
 */
@Slf4j
@Service
public class MiraklSellerDocumentDownloadExtractServiceImpl implements MiraklSellerDocumentDownloadExtractService {

	private final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	private final MiraklKYCSelectionDocumentExecutor miraklKYCSelectionDocumentStrategyExecutor;

	private final MailNotificationUtil kycMailNotificationUtil;

	public MiraklSellerDocumentDownloadExtractServiceImpl(
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
	public KYCDocumentSellerInfoModel getDocumentsSelectedBySeller(
			final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel) {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelWithMiraklShops = populateMiraklShopDocuments(
				kycDocumentSellerInfoModel);

		if (!kycDocumentSellerInfoModelWithMiraklShops.existsDocumentInMirakl()) {
			log.warn("Some needed documents are missing for shop [{}], skipping pushing all documents to hyperwallet",
					kycDocumentSellerInfoModelWithMiraklShops.getClientUserId());
			return kycDocumentSellerInfoModelWithMiraklShops;
		}

		final List<KYCDocumentModel> extractedDocumentsSelectedBySeller = miraklKYCSelectionDocumentStrategyExecutor
				.execute(kycDocumentSellerInfoModelWithMiraklShops).stream().flatMap(List::stream)
				.collect(Collectors.toList());
		//@formatter:on

		return kycDocumentSellerInfoModelWithMiraklShops.toBuilder().documents(extractedDocumentsSelectedBySeller)
				.build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KYCDocumentSellerInfoModel populateMiraklShopDocuments(
			final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel) {
		final MiraklGetShopDocumentsRequest getShopDocumentsRequest = new MiraklGetShopDocumentsRequest(
				List.of(kycDocumentSellerInfoModel.getClientUserId()));

		try {
			log.info("Retrieving documents for seller with id [{}]", kycDocumentSellerInfoModel.getClientUserId());
			final List<MiraklShopDocument> shopDocuments = miraklMarketplacePlatformOperatorApiClient
					.getShopDocuments(getShopDocumentsRequest);
			log.info("Documents retrieved for seller with id [{}]: [{}]", kycDocumentSellerInfoModel.getClientUserId(),
					shopDocuments.stream().map(MiraklShopDocument::getId).collect(Collectors.joining(",")));

			return kycDocumentSellerInfoModel.toBuilder().miraklShopDocuments(shopDocuments).build();
		}
		catch (final MiraklException e) {
			log.error("Something went wrong trying to receive documents from Mirakl for seller with id [{}]",
					kycDocumentSellerInfoModel.getClientUserId());
			kycMailNotificationUtil.sendPlainTextEmail("Issue detected getting documents from Mirakl",
					String.format("Something went wrong getting documents from Mirakl for shop Id [%s]%n%s",
							String.join(",", kycDocumentSellerInfoModel.getClientUserId()),
							MiraklLoggingErrorsUtil.stringify(e)));
		}

		return kycDocumentSellerInfoModel.toBuilder().build();
	}

}
