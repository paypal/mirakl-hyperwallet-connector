package com.paypal.kyc.sellersdocumentextraction.services;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;

import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.services.MiraklDocumentsSelector;
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

	private final MiraklClient miraklMarketplacePlatformOperatorApiClient;

	private final MiraklDocumentsSelector miraklKYCSelectionDocumentStrategyExecutor;

	private final MailNotificationUtil kycMailNotificationUtil;

	public MiraklSellerDocumentDownloadExtractServiceImpl(final MiraklClient miraklMarketplacePlatformOperatorApiClient,
			final MiraklDocumentsSelector miraklKYCSelectionDocumentStrategyExecutor,
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
			log.error(String.format(
					"Something went wrong trying to receive documents from Mirakl for seller with id [%s]",
					kycDocumentSellerInfoModel.getClientUserId()), e);
			kycMailNotificationUtil.sendPlainTextEmail("Issue detected getting documents from Mirakl",
					String.format("Something went wrong getting documents from Mirakl for shop Id [%s]%n%s",
							String.join(",", kycDocumentSellerInfoModel.getClientUserId()),
							MiraklLoggingErrorsUtil.stringify(e)));
		}

		return kycDocumentSellerInfoModel.toBuilder().build();
	}

}
