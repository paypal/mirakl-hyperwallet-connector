package com.paypal.testsupport.archrules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public final class SliceLayeredModuleLayerProtectionRules {

	private SliceLayeredModuleLayerProtectionRules() {
		// Deliberately empty
	}

	@ArchTest
	public static final ArchRule layerAccessProtections = layeredArchitecture().consideringAllDependencies()
			.layer("Controller").definedBy("..controllers..").layer("Persistence").definedBy("..repositories")
			.layer("Connector").definedBy("..connectors..").layer("Service").definedBy("..services..")
			.withOptionalLayers(true).whereLayer("Controller").mayNotBeAccessedByAnyLayer();

	@ArchTest
	public static final ArchRule sliceNoCycles = slices().matching("..com.paypal.*.(*)..").should().beFreeOfCycles();

}
