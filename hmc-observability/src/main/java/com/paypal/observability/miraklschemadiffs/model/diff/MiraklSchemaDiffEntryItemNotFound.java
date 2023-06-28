package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.ToString;

@ToString(callSuper = true)
public class MiraklSchemaDiffEntryItemNotFound extends AbstractSetMiraklSchemaDiffEntry {

	public MiraklSchemaDiffEntryItemNotFound(final MiraklSchemaItem field) {
		super(field);
	}

	@Override
	protected String getSpecificDiffTypeMessage() {
		return "Expected field '%s' has not been found".formatted(item.getCode());
	}

	@Override
	public MiraklSchemaDiffEntryType getDiffType() {
		return MiraklSchemaDiffEntryType.ITEM_NOT_FOUND;
	}

}
