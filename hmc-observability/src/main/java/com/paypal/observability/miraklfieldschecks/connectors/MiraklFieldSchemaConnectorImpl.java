package com.paypal.observability.miraklfieldschecks.connectors;

import com.mirakl.client.mmp.domain.additionalfield.MiraklAdditionalFieldLinkedEntity;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.operator.request.additionalfield.MiraklGetAdditionalFieldRequest;
import com.paypal.infrastructure.mirakl.client.MiraklClient;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MiraklFieldSchemaConnectorImpl implements MiraklFieldSchemaConnector {

	private final MiraklClient miraklOperatorClient;

	public MiraklFieldSchemaConnectorImpl(final MiraklClient miraklOperatorClient) {
		this.miraklOperatorClient = miraklOperatorClient;
	}

	@Override
	public List<MiraklFrontOperatorAdditionalField> getShopCustomFields() {
		final MiraklGetAdditionalFieldRequest miraklGetAdditionalFieldRequest = new MiraklGetAdditionalFieldRequest(
				MiraklAdditionalFieldLinkedEntity.SHOP);
		return miraklOperatorClient.getAdditionalFields(miraklGetAdditionalFieldRequest);
	}

}
