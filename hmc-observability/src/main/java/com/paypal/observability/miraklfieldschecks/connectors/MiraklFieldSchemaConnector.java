package com.paypal.observability.miraklfieldschecks.connectors;

import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;

import java.util.List;

public interface MiraklFieldSchemaConnector {

	List<MiraklFrontOperatorAdditionalField> getShopCustomFields();

}
