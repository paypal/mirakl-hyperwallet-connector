package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.ToString;

@ToString(callSuper = true)
public class MiraklSchemaDiffEntryUnexpectedField extends AbstractSetMiraklSchemaDiffEntry {

	public MiraklSchemaDiffEntryUnexpectedField(final MiraklSchemaItem field) {
		super(field);
	}

	@Override
	protected String getSpecificDiffTypeMessage() {
		return "An unexpected field named '%s' has been found".formatted(item.getCode());
	}

	@Override
	public MiraklSchemaDiffEntryType getDiffType() {
		return MiraklSchemaDiffEntryType.UNEXPECTED_ITEM;
	}

}
