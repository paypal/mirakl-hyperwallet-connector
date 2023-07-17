package com.paypal.observability.miraklschemadiffs.model.report;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MiraklSchemaDiffEntrySeverityAssignerRegistry {

	private final Map<Class<? extends MiraklSchemaItem>, MiraklSchemaDiffEntrySeverityAssigner> registry;

	public MiraklSchemaDiffEntrySeverityAssignerRegistry(
			final List<MiraklSchemaDiffEntrySeverityAssigner> severityAssigners) {
		registry = severityAssigners.stream().collect(
				Collectors.toMap(MiraklSchemaDiffEntrySeverityAssigner::getTargetSchemaType, Function.identity()));
	}

	public MiraklSchemaDiffEntrySeverityAssigner getMiraklSchemaDiffEntrySeverityAssigner(
			final Class<? extends MiraklSchemaItem> schemaType) {
		return registry.get(schemaType);
	}

}
