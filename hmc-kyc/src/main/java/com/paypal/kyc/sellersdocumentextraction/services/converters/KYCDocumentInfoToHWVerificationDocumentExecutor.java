package com.paypal.kyc.sellersdocumentextraction.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.support.strategy.MultipleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KYCDocumentInfoToHWVerificationDocumentExecutor
		extends MultipleAbstractStrategyExecutor<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument> {

	private final Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> strategies;

	public KYCDocumentInfoToHWVerificationDocumentExecutor(
			final Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> getStrategies() {
		return this.strategies;
	}

}
