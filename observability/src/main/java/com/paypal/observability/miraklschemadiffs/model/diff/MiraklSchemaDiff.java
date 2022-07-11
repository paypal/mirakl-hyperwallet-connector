package com.paypal.observability.miraklschemadiffs.model.diff;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiraklSchemaDiff {

	private Class<? extends MiraklSchemaItem> schemaType;

	private List<MiraklSchemaDiffEntry> differences = new ArrayList<>();

	public MiraklSchemaDiff(Class<? extends MiraklSchemaItem> schemaType) {
		this.schemaType = schemaType;
	}

	public void addDifferences(List<MiraklSchemaDiffEntry> diffs) {
		differences.addAll(diffs);
	}

	public List<MiraklSchemaDiffEntry> getDifferences() {
		return Collections.unmodifiableList(differences);
	}

	public Class<? extends MiraklSchemaItem> getSchemaType() {
		return schemaType;
	}

}
