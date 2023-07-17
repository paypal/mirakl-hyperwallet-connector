package com.paypal.jobsystem;

import com.paypal.testsupport.archrules.SliceLayeredModuleLayerProtectionRules;
import com.paypal.testsupport.archrules.SliceLayeredModulePackageStructureRules;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

@AnalyzeClasses(packages = "com.paypal.jobsystem", importOptions = ImportOption.DoNotIncludeTests.class)
public class JobSystemArchTest {

	@ArchTest
	public static final ArchTests packageRules = ArchTests.in(SliceLayeredModulePackageStructureRules.class);

	@ArchTest
	public static final ArchTests layerRules = ArchTests.in(SliceLayeredModuleLayerProtectionRules.class);

}
