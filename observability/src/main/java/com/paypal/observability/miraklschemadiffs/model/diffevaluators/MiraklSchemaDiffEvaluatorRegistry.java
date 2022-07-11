package com.paypal.observability.miraklschemadiffs.model.diffevaluators;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MiraklSchemaDiffEvaluatorRegistry {

	private final Map<Class<? extends MiraklSchemaItem>, List<MiraklSchemaItemDiffEvaluator>> itemDiffEvaluatorsRegistry;

	private final Map<Class<? extends MiraklSchemaItem>, List<MiraklSchemaSetDiffEvaluator>> setDiffEvaluatorsRegistry;

	public MiraklSchemaDiffEvaluatorRegistry(final List<MiraklSchemaItemDiffEvaluator> miraklSchemaItemDiffEvaluators,
			final List<MiraklSchemaSetDiffEvaluator> miraklSchemaSetDiffEvaluators) {

		itemDiffEvaluatorsRegistry = miraklSchemaItemDiffEvaluators.stream()
				.collect(Collectors.groupingBy(MiraklSchemaItemDiffEvaluator::targetClass));
		setDiffEvaluatorsRegistry = miraklSchemaSetDiffEvaluators.stream()
				.collect(Collectors.groupingBy(MiraklSchemaSetDiffEvaluator::targetClass));
	}

	public List<MiraklSchemaItemDiffEvaluator> getItemDiffEvaluators(final MiraklSchemaItem item) {
		//@formatter:off
		return itemDiffEvaluatorsRegistry.entrySet().stream()
				.filter(entry -> entry.getKey().isAssignableFrom(item.getClass()))
				.map(Map.Entry::getValue)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		//@formatter:on
	}

	public List<MiraklSchemaSetDiffEvaluator> getSetDiffEvaluators(final MiraklSchemaDiff diff) {
		//@formatter:off
		return setDiffEvaluatorsRegistry.entrySet().stream()
				.filter(e -> e.getKey().isAssignableFrom(diff.getSchemaType()))
				.map(Map.Entry::getValue)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
