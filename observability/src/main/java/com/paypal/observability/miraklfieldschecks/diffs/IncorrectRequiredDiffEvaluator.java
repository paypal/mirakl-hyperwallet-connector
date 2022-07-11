package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaItemDiffEvaluator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncorrectRequiredDiffEvaluator implements MiraklSchemaItemDiffEvaluator {

	@Override
	public Optional<MiraklSchemaDiffEntry> check(MiraklSchemaItem expected, MiraklSchemaItem actual) {
		MiraklField expectedField = (MiraklField) expected;
		MiraklField actualField = (MiraklField) actual;
		Boolean expectedRequired = expectedField.getRequired() != null ? expectedField.getRequired() : Boolean.FALSE;
		if (!expectedRequired.equals(actualField.getRequired())) {
			return Optional.of(new MiraklSchemaDiffEntryIncorrectAttributeValue(expected, actual, "required"));
		}

		return Optional.empty();
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklField.class;
	}

}
