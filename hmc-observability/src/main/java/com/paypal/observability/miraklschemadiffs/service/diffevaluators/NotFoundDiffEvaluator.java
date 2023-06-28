package com.paypal.observability.miraklschemadiffs.service.diffevaluators;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryItemNotFound;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaSetDiffEvaluator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotFoundDiffEvaluator implements MiraklSchemaSetDiffEvaluator {

	@Override
	public List<MiraklSchemaDiffEntry> getDifferences(final MiraklSchema expected, final MiraklSchema actual) {
		final Set<String> actualFieldsCodes = toCodeSet(actual);
		return expected.getItems().stream().filter(f -> !actualFieldsCodes.contains(f.getCode()))
				.map(MiraklSchemaDiffEntryItemNotFound::new).collect(Collectors.toList());
	}

	private Set<String> toCodeSet(final MiraklSchema docSet) {
		return docSet.getItems().stream().map(MiraklSchemaItem::getCode).collect(Collectors.toSet());
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklSchemaItem.class;
	}

}
