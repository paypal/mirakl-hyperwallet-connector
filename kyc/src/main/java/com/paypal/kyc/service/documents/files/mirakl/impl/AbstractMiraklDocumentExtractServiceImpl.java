package com.paypal.kyc.service.documents.files.mirakl.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.request.shop.document.MiraklDeleteShopDocumentRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklDocumentsExtractService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMiraklDocumentExtractServiceImpl implements MiraklDocumentsExtractService {

	private final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient;

	private final MailNotificationUtil kycMailNotificationUtil;

	protected AbstractMiraklDocumentExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.miraklOperatorClient = miraklOperatorClient;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("java:S3864")
	public void deleteAllDocumentsFromSeller(final List<KYCDocumentInfoModel> successFullPushedListOfDocuments) {
		final List<MiraklShopDocument> miraklShopDocuments = Stream.ofNullable(successFullPushedListOfDocuments)
				.flatMap(Collection::stream).filter(Objects::nonNull).map(KYCDocumentInfoModel::getMiraklShopDocuments)
				.flatMap(Collection::stream).collect(Collectors.toList());

		final List<MiraklDeleteShopDocumentRequest> miraklDeleteShopDocumentRequests = Stream
				.ofNullable(miraklShopDocuments).flatMap(Collection::stream).map(MiraklShopDocument::getId)
				.map(MiraklDeleteShopDocumentRequest::new).collect(Collectors.toList());

		miraklDeleteShopDocumentRequests.stream()
				.peek(deleteUpdatedDocumentRequest -> log.info("Deleting document from Mirakl with id [{}]",
						deleteUpdatedDocumentRequest.getDocumentId()))
				.forEach(miraklOperatorClient::deleteShopDocument);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("java:S3864")
	public void deleteDocuments(final List<MiraklShopDocument> documentsToBeDeleted) {
		final List<MiraklDeleteShopDocumentRequest> miraklDeleteShopDocumentRequests = Stream
				.ofNullable(documentsToBeDeleted).flatMap(Collection::stream).map(MiraklShopDocument::getId)
				.map(MiraklDeleteShopDocumentRequest::new).collect(Collectors.toList());

		miraklDeleteShopDocumentRequests.stream()
				.peek(deleteUpdatedDocumentRequest -> log.info("Deleting document from Mirakl with id [{}]",
						deleteUpdatedDocumentRequest.getDocumentId()))
				.forEach(miraklOperatorClient::deleteShopDocument);
	}

	protected void reportError(final String shopIdentifier, final MiraklException e) {

		kycMailNotificationUtil.sendPlainTextEmail("Issue setting push document flags to false in Mirakl",
				String.format("Something went wrong setting push document flag to false in Mirakl for %s%n%s",
						shopIdentifier, MiraklLoggingErrorsUtil.stringify(e)));
	}

}
