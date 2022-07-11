package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaItemDiffEvaluator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncorrectAllowedValuesDiffEvaluator implements MiraklSchemaItemDiffEvaluator {

	@Override
	public Optional<MiraklSchemaDiffEntry> check(MiraklSchemaItem expected, MiraklSchemaItem actual) {
		if (!hasSameAllowedValues((MiraklField) expected, (MiraklField) actual)) {
			return Optional.of(new MiraklSchemaDiffEntryIncorrectAttributeValue(expected, actual, "allowedValues"));
		}

		return Optional.empty();
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklField.class;
	}

	private boolean hasSameAllowedValues(MiraklField expected, MiraklField actual) {
		return expected.getAllowedValues().isEmpty() || checkAllowedValues(expected, actual);
	}

	private boolean checkAllowedValues(MiraklField expected, MiraklField actual) {
		return expected.getAllowedValues().containsAll(actual.getAllowedValues())
				&& actual.getAllowedValues().containsAll(expected.getAllowedValues());
	}

}
