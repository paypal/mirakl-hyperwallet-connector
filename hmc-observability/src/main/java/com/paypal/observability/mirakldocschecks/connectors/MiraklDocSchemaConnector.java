package com.paypal.observability.mirakldocschecks.connectors;

import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfiguration;

import java.util.List;

public interface MiraklDocSchemaConnector {

	List<MiraklDocumentsConfiguration> getShopDocumentConfigurations();

}
