package com.paypal.observability.miraklschemadiffs.startup;

import com.paypal.observability.mirakldocschecks.startup.MiraklDocSchemaStartupCheckProvider;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportEntry;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReportSeverity;
import com.paypal.observability.startupchecks.model.StartupCheck;
import com.paypal.observability.startupchecks.model.StartupCheckProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractMiraklSchemaStartupCheckPrinterTest {

	@InjectMocks
	MyMiraklSchemaStartupCheckPrinter testObj;

	@Mock
	StartupCheck startupCheckMock;

	@Mock
	MiraklSchemaDiffReportEntry miraklSchemaDiffReportEntry1Mock, miraklSchemaDiffReportEntry2Mock;

	@Mock
	MiraklSchemaDiffEntry miraklSchemaDiffEntry1Mock, miraklSchemaDiffEntry2Mock;

	@Test
	void print_shouldReturnReportEntryMessageAndSeverity() {
		Map<String, Object> detailsMap = Map.of(MiraklDocSchemaStartupCheckProvider.STATUS_CHECK_DETAILS_DIFF_KEY,
				List.of(miraklSchemaDiffReportEntry1Mock, miraklSchemaDiffReportEntry2Mock));
		when(startupCheckMock.getDetails()).thenReturn(detailsMap);
		when(miraklSchemaDiffReportEntry1Mock.getDiff()).thenReturn(miraklSchemaDiffEntry1Mock);
		when(miraklSchemaDiffReportEntry1Mock.getSeverity()).thenReturn(MiraklSchemaDiffReportSeverity.FAIL);
		when(miraklSchemaDiffReportEntry2Mock.getDiff()).thenReturn(miraklSchemaDiffEntry2Mock);
		when(miraklSchemaDiffReportEntry2Mock.getSeverity()).thenReturn(MiraklSchemaDiffReportSeverity.WARN);
		when(miraklSchemaDiffEntry1Mock.getMessage()).thenReturn("MESSAGE-1");
		when(miraklSchemaDiffEntry2Mock.getMessage()).thenReturn("MESSAGE-2");

		String[] result = testObj.print(startupCheckMock);

		assertThat(result).hasSize(2);
		assertThat(result[0]).isEqualTo(String.format("%s%n%s", "MESSAGE-1", "Severity: BLOCKER"));
		assertThat(result[1]).isEqualTo(String.format("%s%n%s", "MESSAGE-2", "Severity: RECOMMENDATION"));
	}

	static class MyMiraklSchemaStartupCheckPrinter extends AbstractMiraklSchemaStartupCheckPrinter {

		@Override
		public Class<? extends StartupCheckProvider> getAssociatedStartupCheck() {
			return null;
		}

	}

}
