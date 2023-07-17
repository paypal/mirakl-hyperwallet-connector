package com.paypal.sellers;

import com.paypal.testsupport.archrules.SliceLayeredModulePackageStructureRules;
import com.paypal.testsupport.archrules.SliceLayeredModuleWeakenedLayerProtectionRules;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

@AnalyzeClasses(packages = "com.paypal.sellers", importOptions = ImportOption.DoNotIncludeTests.class)
public class SellersArchTest {

	@ArchTest
	public static final ArchTests packageRules = ArchTests.in(SliceLayeredModulePackageStructureRules.class);

	@ArchTest
	public static final ArchTests layerRules = ArchTests.in(SliceLayeredModuleWeakenedLayerProtectionRules.class);

}
