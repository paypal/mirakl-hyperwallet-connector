package com.paypal.reports.reportsextract.service.impl;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AbstractHmcFileServiceTest {

	@Spy
	@InjectMocks
	private MyAbstractHmcFileService testObj;

	@Mock
	private CSVPrinter csvPrinterMock;

	private static final String FILE_NAME = "prefixFile_2020-11-10_20-45.csv";

	private static final List<String> CONTENT_LINES = List.of("column11,column12,column13",
			"column21,column22,column23");

	private static final String HEADERS = "headerColumn1,headerColumn2,headerColumn3";

	@Test
	void printCSVFile_shouldCallToApacheCSVToSaveCSVFile() throws IOException {
		final String path = this.getClass().getResource("").getPath();
		doReturn(csvPrinterMock).when(testObj).getCSVPrinter(Paths.get(path), HEADERS, FILE_NAME);
		doNothing().when(csvPrinterMock).printRecords(CONTENT_LINES);
		doNothing().when(csvPrinterMock).flush();

		final var resultFileName = testObj.printCSVFile(Paths.get(path), HEADERS, FILE_NAME, CONTENT_LINES);

		Assertions.assertThat(resultFileName).isEqualTo(FILE_NAME);

	}

	@Test
	void printCSVFile_shouldReturnEmptyStringWhenCSVPrinterCouldNotBeCreated() throws IOException {
		final String path = this.getClass().getResource("").getPath();
		doReturn(null).when(testObj).getCSVPrinter(Paths.get(path), HEADERS, FILE_NAME);

		final var resultFileName = testObj.printCSVFile(Paths.get(path), HEADERS, FILE_NAME, CONTENT_LINES);

		Assertions.assertThat(resultFileName).isEqualTo(StringUtils.EMPTY);

	}

	private static class MyAbstractHmcFileService extends AbstractHmcFileService {

	}

}
