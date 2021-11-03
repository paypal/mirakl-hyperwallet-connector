package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.google.gson.Gson;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl.KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Class that mocks behaviour of sending seller KYC documents to mockserver instead of
 * sending them to HW
 */
@Profile({ "qa" })
@Slf4j
@Service("hyperwalletSellerExtractService")
public class HyperwalletSellerExtractServiceMockImpl extends HyperwalletSellerExtractServiceImpl {

	private static final String FAILING_FILES = "fail";

	private final String mockServerUrl;

	private final RestTemplate restTemplate;

	private static final String HYPERWALLET_PUSH_DOCUMENTS = "/hyperwallet/v4/{userToken}/documents";

	public HyperwalletSellerExtractServiceMockImpl(
			final KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor kycDocumentInfoToHWVerificationDocumentMultipleStrategyFactory,
			@Value("${mockserver.url}") final String mockServerUrl, final RestTemplate restTemplate,
			final MailNotificationUtil kycMailNotificationUtil, final HyperwalletSDKService hyperwalletSDKService) {
		super(kycDocumentInfoToHWVerificationDocumentMultipleStrategyFactory, hyperwalletSDKService,
				kycMailNotificationUtil);
		this.mockServerUrl = mockServerUrl;
		this.restTemplate = restTemplate;
	}

	@Override
	protected KYCDocumentSellerInfoModel callHyperwalletAPI(
			final Map.Entry<KYCDocumentSellerInfoModel, List<HyperwalletVerificationDocument>> entry) {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = entry.getKey();
		final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments = entry.getValue();

		final String postURL = HYPERWALLET_PUSH_DOCUMENTS.replace("{userToken}",
				kycDocumentSellerInfoModel.getUserToken());
		try {
			if (checkFailingFiles(hyperwalletVerificationDocuments)) {
				throw new HyperwalletException("Something bad happened");
			}
			Gson gsonConverter = new Gson();
			restTemplate.postForObject(getMockServerUrl() + postURL, gsonConverter.toJson(entry.getValue()),
					Object.class);
			log.info("Pushed successfully to mockserver documents for shopId  [{}]",
					kycDocumentSellerInfoModel.getClientUserId());

			return kycDocumentSellerInfoModel;
		}
		catch (HyperwalletException ex) {
			log.error("Error uploading document to hyperwallet: [{}]", HyperwalletLoggingErrorsUtil.stringify(ex));
			getKycMailNotificationUtil().sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
					String.format("Something went wrong pushing documents to Hyperwallet for shop Id [%s]%n%s",
							String.join(",", kycDocumentSellerInfoModel.getClientUserId()),
							HyperwalletLoggingErrorsUtil.stringify(ex)));

			return null;
		}

	}

	private boolean checkFailingFiles(final List<HyperwalletVerificationDocument> originalFiles) {
		//@formatter:off
        return Optional.ofNullable(originalFiles).orElse(List.of()).stream()
                .map(HyperwalletVerificationDocument::getUploadFiles)
                .filter(Objects::nonNull)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(fileName -> fileName.contains(FAILING_FILES));


        //@formatter:on
	}

	protected String getMockServerUrl() {
		return mockServerUrl;
	}

}
