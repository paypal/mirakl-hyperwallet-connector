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
		return String.format("Item '%s' doesn't have the expected definition.%n%s", expected.getCode(),
				getAttributeValueDetails());
	}

	private String getAttributeValueDetails() {
		return String.format("Property '%s' doesn't have the correct value.%nExpected value: '%s'%nActual value: '%s'",
				attributeName, getFieldValue(expected), getFieldValue(actual));
	}

	@Override
	public MiraklSchemaDiffEntryType getDiffType() {
		return MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE;
	}

	private String getFieldValue(MiraklSchemaItem item) {
		Object fieldValue = readProperty(item);
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

	private Object readProperty(MiraklSchemaItem item) {
		try {
			PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(item.getClass(), attributeName);
			return pd != null ? pd.getReadMethod().invoke(item) : null;
		}
		catch (Exception e) {
			return null;
		}
	}

}
