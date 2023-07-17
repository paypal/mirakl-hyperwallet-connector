package com.paypal.observability.miraklschemadiffs.model.diffevaluators;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchema;
import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;

import java.util.List;

public interface MiraklSchemaSetDiffEvaluator {

	List<MiraklSchemaDiffEntry> getDifferences(MiraklSchema expected, MiraklSchema actual);

	Class<? extends MiraklSchemaItem> targetClass();

}
