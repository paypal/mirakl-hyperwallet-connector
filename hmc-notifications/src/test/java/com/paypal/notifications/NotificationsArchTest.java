package com.paypal.notifications;

import com.paypal.testsupport.archrules.SliceLayeredModuleLayerProtectionRules;
import com.paypal.testsupport.archrules.SliceLayeredModulePackageStructureRules;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

//@formatter:off
@AnalyzeClasses(packages = "com.paypal.notifications",
		importOptions = ImportOption.DoNotIncludeTests.class)
class NotificationsArchTest {

	@ArchTest
	public static final ArchTests packageRules = ArchTests.in(SliceLayeredModulePackageStructureRules.class);

	@ArchTest
	public static final ArchTests layerRules = ArchTests.in(SliceLayeredModuleLayerProtectionRules.class);

}
