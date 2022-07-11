package com.paypal.observability.startupchecks.model;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StartupCheckPrinterRegistry {

	private Map<String, StartupCheckPrinter> registry;

	public StartupCheckPrinterRegistry(final List<StartupCheckPrinter> startupCheckPrinters,
			final List<StartupCheckProvider> startupCheckProviders) {
		Map<Class<? extends StartupCheckProvider>, StartupCheckProvider> startupCheckProviderClassMap = startupCheckProviders
				.stream().collect(Collectors.toMap(StartupCheckProvider::getClass, Function.identity()));

		registry = startupCheckPrinters.stream().collect(Collectors.toMap(
				x -> startupCheckProviderClassMap.get(x.getAssociatedStartupCheck()).getName(), Function.identity()));
	}

	public StartupCheckPrinter getStartupCheckPrinter(String name) {
		return registry.get(name);
	}

}
