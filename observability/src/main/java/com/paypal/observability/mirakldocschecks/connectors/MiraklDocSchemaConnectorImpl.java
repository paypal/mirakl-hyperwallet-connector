package com.paypal.observability.mirakldocschecks.connectors;

import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfiguration;
import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfigurations;
import com.mirakl.client.mmp.operator.request.documents.MiraklGetDocumentsConfigurationRequest;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MiraklDocSchemaConnectorImpl implements MiraklDocSchemaConnector {

	private final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient;

	public MiraklDocSchemaConnectorImpl(final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient) {
		this.miraklOperatorClient = miraklOperatorClient;
	}

	@Override
	public List<MiraklDocumentsConfiguration> getShopDocumentConfigurations() {

		final MiraklGetDocumentsConfigurationRequest miraklGetDocumentsConfigurationRequest = new MiraklGetDocumentsConfigurationRequest();
		final MiraklDocumentsConfigurations documentsConfiguration = miraklOperatorClient
				.getDocumentsConfiguration(miraklGetDocumentsConfigurationRequest);
		if (documentsConfiguration.getDocuments() != null) {
			return documentsConfiguration.getDocuments();
		}

		return List.of();
	}

}
