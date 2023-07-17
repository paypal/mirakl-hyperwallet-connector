package com.paypal.kyc.stakeholdersdocumentextraction.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.kyc.documentextractioncommons.services.HyperwalletDocumentUploadService;
import com.paypal.kyc.documentextractioncommons.support.AbstractHyperwalletDocumentExtractService;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.stakeholdersdocumentextraction.services.converters.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder.VerificationStatus.REQUIRED;

/**
 * Implementation of {@link HyperwalletBusinessStakeholderExtractService}
 */
@Slf4j
@Service
@Getter
public class HyperwalletBusinessStakeholderExtractServiceImpl
		extends AbstractHyperwalletDocumentExtractService<KYCDocumentBusinessStakeHolderInfoModel>
		implements HyperwalletBusinessStakeholderExtractService {

	private final KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;

	public HyperwalletBusinessStakeholderExtractServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final HyperwalletDocumentUploadService hyperwalletDocumentUploadService,
			final KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor) {
		super(userHyperwalletSDKService, hyperwalletDocumentUploadService);
		this.kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor = kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getKYCRequiredVerificationBusinessStakeHolders(final String hyperwalletProgram,
			final String userToken) {
		final Hyperwallet hyperwallet = getHyperwalletSDKService()
				.getHyperwalletInstanceByHyperwalletProgram(hyperwalletProgram);
		final List<HyperwalletBusinessStakeholder> businessStakeholders = getBusinessStakeholders(userToken,
				hyperwallet);

		return businessStakeholders.stream()
				.filter(hyperwalletBusinessStakeholder -> REQUIRED
						.equals(hyperwalletBusinessStakeholder.getVerificationStatus()))
				.map(HyperwalletBusinessStakeholder::getToken).collect(Collectors.toList());
	}

	private List<HyperwalletBusinessStakeholder> getBusinessStakeholders(final String userToken,
			final Hyperwallet hyperwallet) {
		final HyperwalletList<HyperwalletBusinessStakeholder> businessStakeHolders = hyperwallet
				.listBusinessStakeholders(userToken);

		return Optional.ofNullable(businessStakeHolders).map(HyperwalletList::getData)
				.filter(CollectionUtils::isNotEmpty).orElse(Collections.emptyList());
	}

	@Override
	protected List<HyperwalletVerificationDocument> getHyperwalletVerificationDocuments(
			final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeHolderInfoModel) {
		return kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor
				.execute(kycBusinessStakeHolderInfoModel);
	}

}
