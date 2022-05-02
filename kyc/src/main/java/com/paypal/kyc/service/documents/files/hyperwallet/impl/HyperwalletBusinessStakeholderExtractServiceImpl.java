package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
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
@Profile({ "!qa" })
public class HyperwalletBusinessStakeholderExtractServiceImpl
		extends AbstractHyperwalletDocumentExtractService<KYCDocumentBusinessStakeHolderInfoModel>
		implements HyperwalletBusinessStakeholderExtractService {

	private final KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;

	public HyperwalletBusinessStakeholderExtractServiceImpl(HyperwalletSDKService hyperwalletSDKService,
			HyperwalletDocumentUploadService hyperwalletDocumentUploadService,
			KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor) {
		super(hyperwalletSDKService, hyperwalletDocumentUploadService);
		this.kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor = kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getKYCRequiredVerificationBusinessStakeHolders(final String hyperwalletProgram,
			final String userToken) {
		final Hyperwallet hyperwallet = getHyperwalletSDKService().getHyperwalletInstance(hyperwalletProgram);
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
			KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeHolderInfoModel) {
		return kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor
				.execute(kycBusinessStakeHolderInfoModel);
	}

}
