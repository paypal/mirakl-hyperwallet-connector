package com.paypal.testsupport.archrules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public final class SliceLayeredModuleWeakenedLayerProtectionRules {

	private SliceLayeredModuleWeakenedLayerProtectionRules() {
		// Deliberately empty
	}

	@ArchTest
	public static final ArchRule layerAccessProtections = layeredArchitecture().consideringAllDependencies()
			.layer("Controller").definedBy("..controllers..").layer("Persistence").definedBy("..repositories")
			.layer("Connector").definedBy("..connectors..").layer("Service").definedBy("..services..")
			.withOptionalLayers(true).whereLayer("Controller").mayNotBeAccessedByAnyLayer();

}
