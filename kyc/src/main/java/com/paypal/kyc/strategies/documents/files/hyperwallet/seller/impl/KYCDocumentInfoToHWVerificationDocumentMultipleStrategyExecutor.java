package com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.strategy.MultipleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor
		extends MultipleAbstractStrategyExecutor<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument> {

	private final Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> strategies;

	public KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor(
			final Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> getStrategies() {
		return this.strategies;
	}

}
