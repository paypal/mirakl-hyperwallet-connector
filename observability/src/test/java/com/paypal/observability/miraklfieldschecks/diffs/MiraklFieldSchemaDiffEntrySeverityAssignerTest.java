package com.paypal.observability.miraklfieldschecks.diffs;

import com.paypal.observability.miraklfieldschecks.model.MiraklField;
import com.paypal.observability.miraklfieldschecks.model.MiraklFieldPermissions;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryIncorrectAttributeValue;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryType;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklFieldSchemaDiffEntrySeverityAssignerTest {

	@InjectMocks
	private MiraklFieldSchemaDiffEntrySeverityAssigner testObj;

	@Mock
	private MiraklSchemaDiffEntryIncorrectAttributeValue miraklSchemaDiffEntryIncorrectAttributeValueMock;

	@Mock
	private MiraklField miraklFieldActualMock, miraklFieldExpectedMock;

	@ParameterizedTest
	@MethodSource
	void getSeverityFor_shouldReturnBlockerWhenAddingMorePermissionsThanTheExpectedOnes(
			MiraklFieldPermissions expectedPermissions, MiraklFieldPermissions actualPermissions,
			MiraklSchemaDiffReportSeverity expectedSeverity) {
		when(miraklSchemaDiffEntryIncorrectAttributeValueMock.getDiffType())
				.thenReturn(MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE);
		when(miraklSchemaDiffEntryIncorrectAttributeValueMock.getAttributeName()).thenReturn("permissions");
		when(miraklSchemaDiffEntryIncorrectAttributeValueMock.getActual()).thenReturn(miraklFieldActualMock);
		when(miraklSchemaDiffEntryIncorrectAttributeValueMock.getExpected()).thenReturn(miraklFieldExpectedMock);

		when(miraklFieldExpectedMock.getPermissions()).thenReturn(expectedPermissions);
		when(miraklFieldActualMock.getPermissions()).thenReturn(actualPermissions);

		MiraklSchemaDiffReportSeverity result = testObj
				.getSeverityFor(miraklSchemaDiffEntryIncorrectAttributeValueMock);
		assertThat(result).isEqualTo(expectedSeverity);
	}

	private static Stream<Arguments> getSeverityFor_shouldReturnBlockerWhenAddingMorePermissionsThanTheExpectedOnes() {
		//@formatter:off
		return Stream.of(
				Arguments.of(MiraklFieldPermissions.INVISIBLE, MiraklFieldPermissions.READ_ONLY, MiraklSchemaDiffReportSeverity.FAIL),
				Arguments.of(MiraklFieldPermissions.INVISIBLE, MiraklFieldPermissions.READ_WRITE, MiraklSchemaDiffReportSeverity.FAIL),
				Arguments.of(MiraklFieldPermissions.READ_ONLY, MiraklFieldPermissions.INVISIBLE, MiraklSchemaDiffReportSeverity.WARN),
				Arguments.of(MiraklFieldPermissions.READ_ONLY, MiraklFieldPermissions.READ_WRITE, MiraklSchemaDiffReportSeverity.FAIL),
				Arguments.of(MiraklFieldPermissions.READ_WRITE, MiraklFieldPermissions.READ_ONLY, MiraklSchemaDiffReportSeverity.WARN),
				Arguments.of(MiraklFieldPermissions.READ_WRITE, MiraklFieldPermissions.INVISIBLE, MiraklSchemaDiffReportSeverity.WARN)
		);
		//@formatter:on
	}

}
