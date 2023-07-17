package com.paypal.kyc.documentextractioncommons.services;

import com.paypal.infrastructure.support.strategy.MultipleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MiraklDocumentsSelector
		extends MultipleAbstractStrategyExecutor<KYCDocumentInfoModel, List<KYCDocumentModel>> {

	private final Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> strategies;

	public MiraklDocumentsSelector(final Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> getStrategies() {
		return this.strategies;
	}

}
