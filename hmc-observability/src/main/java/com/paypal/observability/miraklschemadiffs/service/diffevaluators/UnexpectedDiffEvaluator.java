package com.paypal.observability.miraklschemadiffs.service.diffevaluators;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryUnexpectedField;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaSetDiffEvaluator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UnexpectedDiffEvaluator implements MiraklSchemaSetDiffEvaluator {

	@Override
	public List<MiraklSchemaDiffEntry> getDifferences(final MiraklSchema expected, final MiraklSchema actual) {
		final Set<String> expectedFieldsCodes = toCodeSet(expected);
		//@formatter:off
		return actual.getItems().stream()
				.filter(f -> !expectedFieldsCodes.contains(f.getCode()))
				.map(MiraklSchemaDiffEntryUnexpectedField::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

	private Set<String> toCodeSet(final MiraklSchema customFieldsSet) {
		return customFieldsSet.getItems().stream().map(MiraklSchemaItem::getCode).collect(Collectors.toSet());
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklSchemaItem.class;
	}

}
