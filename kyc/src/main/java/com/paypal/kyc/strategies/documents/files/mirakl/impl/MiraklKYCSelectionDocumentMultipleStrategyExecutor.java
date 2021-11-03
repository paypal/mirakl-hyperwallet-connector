package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.paypal.infrastructure.strategy.MultipleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MiraklKYCSelectionDocumentMultipleStrategyExecutor
		extends MultipleAbstractStrategyExecutor<KYCDocumentInfoModel, List<KYCDocumentModel>> {

	private final Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> strategies;

	public MiraklKYCSelectionDocumentMultipleStrategyExecutor(
			final Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> getStrategies() {
		return this.strategies;
	}

}
