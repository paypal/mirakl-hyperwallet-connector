package com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.strategy.MultipleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory extends
		MultipleAbstractStrategyFactory<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument> {

	private final Set<Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument>> strategies;

	public KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory(
			final Set<Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument>> getStrategies() {
		return this.strategies;
	}

}
