package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.ToString;

@ToString(callSuper = true)
public class MiraklSchemaDiffEntryUnexpectedField extends AbstractSetMiraklSchemaDiffEntry {

	public MiraklSchemaDiffEntryUnexpectedField(MiraklSchemaItem field) {
		super(field);
	}

	@Override
	protected String getSpecificDiffTypeMessage() {
		return String.format("An unexpected field named '%s' has been found", item.getCode());
	}

	@Override
	public MiraklSchemaDiffEntryType getDiffType() {
		return MiraklSchemaDiffEntryType.UNEXPECTED_ITEM;
	}

}
