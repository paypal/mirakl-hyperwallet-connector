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
		return String.format("%s%nOffending field details: %s", getSpecificDiffTypeMessage(), item.toString());
	}

	protected abstract String getSpecificDiffTypeMessage();

}
