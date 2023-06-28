package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaItemDiffEvaluator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncorrectTypeDiffEvaluator implements MiraklSchemaItemDiffEvaluator {

	@Override
	public Optional<MiraklSchemaDiffEntry> check(final MiraklSchemaItem expected, final MiraklSchemaItem actual) {
		if (!((MiraklField) expected).getType().equals(((MiraklField) actual).getType())) {
			return Optional.of(new MiraklSchemaDiffEntryIncorrectAttributeValue(expected, actual, "type"));
		}

		return Optional.empty();
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklField.class;
	}

}
