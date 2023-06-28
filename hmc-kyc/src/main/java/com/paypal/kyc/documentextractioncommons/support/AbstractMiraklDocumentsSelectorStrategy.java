package com.paypal.kyc.documentextractioncommons.support;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;

import com.mirakl.client.mmp.request.shop.document.MiraklDownloadShopsDocumentsRequest;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractMiraklDocumentsSelectorStrategy
		implements Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>> {

	private final MiraklClient miraklApiClient;

	protected AbstractMiraklDocumentsSelectorStrategy(final MiraklClient miraklApiClient) {
		this.miraklApiClient = miraklApiClient;
	}

	@Override
	public List<KYCDocumentModel> execute(final KYCDocumentInfoModel source) {

		final List<String> miraklFields = getMiraklFieldNames(source);
		final Map<String, String> fieldNameAnDocumentIdList = getDocumentIds(source.getMiraklShopDocuments(),
				miraklFields);

		final List<Pair<String, MiraklDownloadShopsDocumentsRequest>> miraklDownloadShopRequests = fieldNameAnDocumentIdList
				.entrySet().stream().map(fieldNameAndDocumentIdPair -> {
					final MiraklDownloadShopsDocumentsRequest miraklDownloadDocumentRequest = new MiraklDownloadShopsDocumentsRequest();
					miraklDownloadDocumentRequest.setDocumentIds(List.of(fieldNameAndDocumentIdPair.getKey()));
					return Pair.of(fieldNameAndDocumentIdPair.getValue(), miraklDownloadDocumentRequest);
				}).collect(Collectors.toList());

		// @formatter:off
		return miraklDownloadShopRequests.stream()
				.map(this::downloadDocument)
				.collect(Collectors.toList());
		// @formatter:on
	}

	protected abstract List<String> getMiraklFieldNames(KYCDocumentInfoModel source);

	private Map<String, String> getDocumentIds(final List<MiraklShopDocument> miraklShopDocuments,
			final List<String> miraklFields) {
		final Map<String, String> existingDocuments = miraklShopDocuments.stream()
				.collect(Collectors.toMap(MiraklShopDocument::getTypeCode, MiraklShopDocument::getId));

		//@formatter:off
		return miraklFields.stream()
				.collect(Collectors.toMap(existingDocuments::get, Function.identity()))
				.entrySet()
				.stream()
				.filter(miraklField -> Objects.nonNull(miraklField.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		//@formatter:on
	}

	private KYCDocumentModel downloadDocument(
			final Pair<String, MiraklDownloadShopsDocumentsRequest> fieldNameAndDocumentIdPair) {
		final MiraklDownloadShopsDocumentsRequest miraklDownloadShopsDocumentsRequest = fieldNameAndDocumentIdPair
				.getValue();
		final String fieldName = fieldNameAndDocumentIdPair.getKey();
		final String documentId = miraklDownloadShopsDocumentsRequest.getDocumentIds().stream().findAny().orElse(null);
		try {
			log.info("Trying to download file with id [{}]", documentId);
			final FileWrapper fileWrapper = miraklApiClient.downloadShopsDocuments(miraklDownloadShopsDocumentsRequest);
			log.info("Document with id [{}] downloaded", documentId);

			return KYCDocumentModel.builder().file(fileWrapper.getFile()).documentFieldName(fieldName).build();
		}
		catch (final MiraklException e) {
			log.error("Something went wrong trying to download document with id [%s]".formatted(documentId), e);
			log.error(MiraklLoggingErrorsUtil.stringify(e), e);
			throw new HMCMiraklAPIException(e);
		}
	}

}
