package com.paypal.observability.miraklfieldschecks.services;

import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.paypal.observability.miraklfieldschecks.repository.MiraklFieldSchemaRepository;
import com.paypal.observability.miraklfieldschecks.services.converters.MiraklFieldSchemaConnectorConverter;
import com.paypal.observability.miraklfieldschecks.services.converters.MiraklFieldSchemaRepositoryConverter;
import com.paypal.observability.miraklfieldschecks.connectors.MiraklFieldSchemaConnector;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldSchemaYaml;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiff;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.miraklschemadiffs.service.MiraklSchemaComparator;
import com.paypal.observability.miraklschemadiffs.service.MiraklSchemaDiffReportBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiraklFieldSchemaCheckerServiceImpl implements MiraklFieldSchemaCheckerService {

	@Value("${hyperwallet.kycAutomated}")
	protected boolean isKycAutomated;

	private final MiraklSchemaComparator miraklFieldSchemaComparator;

	private final MiraklSchemaDiffReportBuilder miraklFieldSchemaDiffReportBuilder;

	private final MiraklFieldSchemaConnector miraklFieldSchemaConnector;

	private final MiraklFieldSchemaRepository miraklFieldSchemaRepository;

	private final MiraklFieldSchemaRepositoryConverter miraklFieldSchemaRepositoryConverter;

	private final MiraklFieldSchemaConnectorConverter miraklFieldSchemaConnectorConverter;

	public MiraklFieldSchemaCheckerServiceImpl(final MiraklSchemaComparator miraklFieldSchemaComparator,
			final MiraklSchemaDiffReportBuilder miraklFieldSchemaDiffReportBuilder,
			final MiraklFieldSchemaConnector miraklFieldSchemaConnector,
			final MiraklFieldSchemaRepository miraklFieldSchemaRepository,
			final MiraklFieldSchemaRepositoryConverter miraklFieldSchemaRepositoryConverter,
			final MiraklFieldSchemaConnectorConverter miraklFieldSchemaConnectorConverter) {
		this.miraklFieldSchemaComparator = miraklFieldSchemaComparator;
		this.miraklFieldSchemaDiffReportBuilder = miraklFieldSchemaDiffReportBuilder;
		this.miraklFieldSchemaConnector = miraklFieldSchemaConnector;
		this.miraklFieldSchemaRepository = miraklFieldSchemaRepository;
		this.miraklFieldSchemaRepositoryConverter = miraklFieldSchemaRepositoryConverter;
		this.miraklFieldSchemaConnectorConverter = miraklFieldSchemaConnectorConverter;
	}

	@Override
	public MiraklSchemaDiffReport checkMiraklSchema() {
		final List<MiraklFrontOperatorAdditionalField> miraklFields = miraklFieldSchemaConnector.getShopCustomFields();
		final MiraklSchema remoteSchema = miraklFieldSchemaConnectorConverter.from(miraklFields);

		final MiraklFieldSchemaYaml miraklFieldSchemaYaml = miraklFieldSchemaRepository
				.loadCustomFieldsSchema(isKycAutomated);
		final MiraklSchema expectedSchema = miraklFieldSchemaRepositoryConverter.from(miraklFieldSchemaYaml);

		final MiraklSchemaDiff miraklFieldSchemaDiff = miraklFieldSchemaComparator.compareSchemas(expectedSchema,
				remoteSchema);

		return miraklFieldSchemaDiffReportBuilder.getSchemaReport(miraklFieldSchemaDiff);
	}

}
