package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor
public class MiraklSchemaDiffEntryIncorrectAttributeValue implements MiraklSchemaDiffEntry {

	private final MiraklSchemaItem expected;

	private final MiraklSchemaItem actual;

	private final String attributeName;

	@Override
	public String getMessage() {
		return "Item '%s' doesn't have the expected definition.%n%s".formatted(expected.getCode(),
				getAttributeValueDetails());
	}

	private String getAttributeValueDetails() {
		return "Property '%s' doesn't have the correct value.%nExpected value: '%s'%nActual value: '%s'"
				.formatted(attributeName, getFieldValue(expected), getFieldValue(actual));
	}

	@Override
	public MiraklSchemaDiffEntryType getDiffType() {
		return MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE;
	}

	private String getFieldValue(final MiraklSchemaItem item) {
		final Object fieldValue = readProperty(item);
		if (fieldValue == null) {
			return "";
		}
		else if (fieldValue instanceof List) {
			return ((List<?>) fieldValue).stream().map(Object::toString).collect(Collectors.joining(","));
		}
		else {
			return fieldValue.toString();
		}
	}

	private Object readProperty(final MiraklSchemaItem item) {
		try {
			final PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(item.getClass(), attributeName);
			return pd != null ? pd.getReadMethod().invoke(item) : null;
		}
		catch (final Exception e) {
			return null;
		}
	}

}
