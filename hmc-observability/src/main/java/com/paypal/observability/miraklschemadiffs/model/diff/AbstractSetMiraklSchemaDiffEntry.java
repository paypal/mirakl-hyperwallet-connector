package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class AbstractSetMiraklSchemaDiffEntry implements MiraklSchemaDiffEntry {

	protected MiraklSchemaItem item;

	@Override
	public String getMessage() {
		return "%s%nOffending field details: %s".formatted(getSpecificDiffTypeMessage(), item.toString());
	}

	protected abstract String getSpecificDiffTypeMessage();

}
