package com.paypal.testsupport.archrules;

import com.paypal.infrastructure.support.converter.Converter;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public final class SliceLayeredModulePackageStructureRules {

	private SliceLayeredModulePackageStructureRules() {
		// Deliberately empty
	}

	@ArchTest
	public static final ArchRule servicesOnCorrectPackage = classes().that().areAnnotatedWith(Service.class).should()
			.resideInAnyPackage("..services..").allowEmptyShould(true);

	@ArchTest
	public static final ArchRule repositoriesOnCorrectPackage = classes().that().areAnnotatedWith(Repository.class)
			.should().resideInAnyPackage("..repositories..").allowEmptyShould(true);

	@ArchTest
	public static final ArchRule controllersOnCorrectPackage = classes().that().areAnnotatedWith(RestController.class)
			.should().resideInAnyPackage("..controllers..").allowEmptyShould(true);

	@ArchTest
	public static final ArchRule convertersOnCorrectPackage = classes().that().implement(Converter.class).should()
			.resideInAnyPackage("..converters..").allowEmptyShould(true);

}
