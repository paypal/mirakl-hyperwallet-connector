package com.paypal.observability.miraklschemadiffs.model.diffevaluators;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;

import java.util.Optional;

public interface MiraklSchemaItemDiffEvaluator {

	Optional<MiraklSchemaDiffEntry> check(MiraklSchemaItem expected, MiraklSchemaItem actual);

	Class<? extends MiraklSchemaItem> targetClass();

}
