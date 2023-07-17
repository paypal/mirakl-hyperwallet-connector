package com.paypal.observability.mirakldocschecks.services;

import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfiguration;
import com.paypal.observability.mirakldocschecks.connectors.MiraklDocSchemaConnector;
import com.paypal.observability.mirakldocschecks.repository.MiraklDocSchemaRepository;
import com.paypal.observability.mirakldocschecks.repository.model.MiraklDocSchemaYaml;
import com.paypal.observability.mirakldocschecks.services.converters.MiraklDocSchemaConnectorConverter;
import com.paypal.observability.mirakldocschecks.services.converters.MiraklDocSchemaRepositoryConverter;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiff;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.miraklschemadiffs.service.MiraklSchemaComparator;
import com.paypal.observability.miraklschemadiffs.service.MiraklSchemaDiffReportBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiraklDocSchemaCheckerServiceImpl implements MiraklDocSchemaCheckerService {

	@Value("${hmc.toggle-features.automated-kyc}")
	protected boolean isKycAutomated;

	private final MiraklSchemaComparator miraklSchemaComparator;

	private final MiraklSchemaDiffReportBuilder miraklSchemaDiffReportBuilder;

	private final MiraklDocSchemaConnector miraklDocSchemaConnector;

	private final MiraklDocSchemaRepository miraklDocSchemaRepository;

	private final MiraklDocSchemaRepositoryConverter miraklDocSchemaRepositoryConverter;

	private final MiraklDocSchemaConnectorConverter miraklDocSchemaConnectorConverter;

	public MiraklDocSchemaCheckerServiceImpl(final MiraklSchemaComparator miraklSchemaComparator,
			final MiraklSchemaDiffReportBuilder miraklSchemaDiffReportBuilder,
			final MiraklDocSchemaConnector miraklDocSchemaConnector,
			final MiraklDocSchemaRepository miraklDocSchemaRepository,
			final MiraklDocSchemaRepositoryConverter miraklDocSchemaRepositoryConverter,
			final MiraklDocSchemaConnectorConverter miraklDocSchemaConnectorConverter) {
		this.miraklSchemaComparator = miraklSchemaComparator;
		this.miraklSchemaDiffReportBuilder = miraklSchemaDiffReportBuilder;
		this.miraklDocSchemaConnector = miraklDocSchemaConnector;
		this.miraklDocSchemaRepository = miraklDocSchemaRepository;
		this.miraklDocSchemaRepositoryConverter = miraklDocSchemaRepositoryConverter;
		this.miraklDocSchemaConnectorConverter = miraklDocSchemaConnectorConverter;
	}

	@Override
	public MiraklSchemaDiffReport checkMiraklDocs() {
		return isKycAutomated ? getMiraklDocSchemaDiffReport() : new MiraklSchemaDiffReport();
	}

	private MiraklSchemaDiffReport getMiraklDocSchemaDiffReport() {
		final List<MiraklDocumentsConfiguration> documents = miraklDocSchemaConnector.getShopDocumentConfigurations();
		final MiraklSchema remoteDocSchema = miraklDocSchemaConnectorConverter.from(documents);

		final MiraklDocSchemaYaml miraklDocSchemaYaml = miraklDocSchemaRepository.loadCustomFieldsSchema();
		final MiraklSchema expectedDocSchema = miraklDocSchemaRepositoryConverter.from(miraklDocSchemaYaml);

		final MiraklSchemaDiff miraklDocSchemaDiff = miraklSchemaComparator.compareSchemas(expectedDocSchema,
				remoteDocSchema);

		return miraklSchemaDiffReportBuilder.getSchemaReport(miraklDocSchemaDiff);
	}

}
