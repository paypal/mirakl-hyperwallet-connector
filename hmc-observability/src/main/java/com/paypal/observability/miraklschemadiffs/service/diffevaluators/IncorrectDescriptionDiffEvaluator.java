package com.paypal.observability.miraklschemadiffs.service.diffevaluators;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diffevaluators.MiraklSchemaItemDiffEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncorrectDescriptionDiffEvaluator implements MiraklSchemaItemDiffEvaluator {

	@Override
	public Optional<MiraklSchemaDiffEntry> check(final MiraklSchemaItem expected, final MiraklSchemaItem actual) {
		final String expectedSanitizedDescription = sanitize(expected.getDescription());
		final String actualSanitizedDescription = sanitize(actual.getDescription());
		if (!expectedSanitizedDescription.equals(actualSanitizedDescription)) {
			return Optional.of(new MiraklSchemaDiffEntryIncorrectAttributeValue(expected, actual, "description"));
		}

		return Optional.empty();
	}

	@Override
	public Class<? extends MiraklSchemaItem> targetClass() {
		return MiraklSchemaItem.class;
	}

	private String sanitize(final String description) {
		String sanitizedString = description;

		sanitizedString = sanitizeLastChar(sanitizedString, '\n');
		sanitizedString = sanitizeLastChar(sanitizedString, '.');

		return sanitizedString;
	}

	private String sanitizeLastChar(final String string, final char lastChar) {
		if (StringUtils.isNotBlank(string)) {
			String sanitizedString = string;
			final int lastIndexNewLine = string.lastIndexOf(lastChar);
			if (lastIndexNewLine == string.length() - 1) {
				sanitizedString = string.substring(0, lastIndexNewLine);
			}

			return sanitizedString;
		}
		else {
			return "";
		}
	}

}
