package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaItemDiffEvaluator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncorrectRegexpDiffEvaluator implements MiraklSchemaItemDiffEvaluator {

	@Override
	public Optional<MiraklSchemaDiffEntry> check(final MiraklSchemaItem expected, final MiraklSchemaItem actual) {
		final MiraklField expectedField = (MiraklField) expected;
		final MiraklField actualField = (MiraklField) actual;
		final String expectedRegexpPattern = expectedField.getRegexpPattern() != null ? expectedField.getRegexpPattern()
				: "";
		final String actualRegexpPattern = actualField.getRegexpPattern() != null ? actualField.getRegexpPattern() : "";
		if (!expectedRegexpPattern.equals(actualRegexpPattern)) {
			return Optional.of(new MiraklSchemaDiffEntryIncorrectAttributeValue(expected, actual, "regexp"));
		}

		return Optional.empty();
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklField.class;
	}

}
