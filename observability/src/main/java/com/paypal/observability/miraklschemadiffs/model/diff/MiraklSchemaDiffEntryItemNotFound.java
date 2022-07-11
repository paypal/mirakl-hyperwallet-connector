package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.ToString;

@ToString(callSuper = true)
public class MiraklSchemaDiffEntryItemNotFound extends AbstractSetMiraklSchemaDiffEntry {

	public MiraklSchemaDiffEntryItemNotFound(MiraklSchemaItem field) {
		super(field);
	}

	@Override
	protected String getSpecificDiffTypeMessage() {
		return String.format("Expected field '%s' has not been found", item.getCode());
	}

	@Override
	public MiraklSchemaDiffEntryType getDiffType() {
		return MiraklSchemaDiffEntryType.ITEM_NOT_FOUND;
	}

}
