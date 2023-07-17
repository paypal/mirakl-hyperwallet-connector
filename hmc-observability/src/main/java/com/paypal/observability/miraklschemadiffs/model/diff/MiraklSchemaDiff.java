package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiraklSchemaDiff {

	private final Class<? extends MiraklSchemaItem> schemaType;

	private final List<MiraklSchemaDiffEntry> differences = new ArrayList<>();

	public MiraklSchemaDiff(final Class<? extends MiraklSchemaItem> schemaType) {
		this.schemaType = schemaType;
	}

	public void addDifferences(final List<MiraklSchemaDiffEntry> diffs) {
		differences.addAll(diffs);
	}

	public List<MiraklSchemaDiffEntry> getDifferences() {
		return Collections.unmodifiableList(differences);
	}

	public Class<? extends MiraklSchemaItem> getSchemaType() {
		return schemaType;
	}

}
