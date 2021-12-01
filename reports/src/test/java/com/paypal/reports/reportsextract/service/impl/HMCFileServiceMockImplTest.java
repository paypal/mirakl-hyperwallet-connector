package com.paypal.reports.reportsextract.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HMCFileServiceMockImplTest {

	private static final String PREFIX_FILE = "prefixFile";

	private static final String FILE_NAME = "prefixFile.csv";

	private static final List<String> CONTENT_LINES = List.of("column11,column12,column13",
			"column21,column22,column23");

	private static final String HEADERS = "headerColumn1,headerColumn2,headerColumn3";

	@Spy
	@InjectMocks
	private HMCFileServiceMockImpl testObj;

	@Test
	void saveCSVFile_shouldSaveCSVFile_whenPathAndContentAreNotNull() throws IOException {
		final String path = getClass().getResource("").getPath();
		doReturn(FILE_NAME).when(testObj).printCSVFile(Paths.get(path + FILE_NAME), HEADERS, FILE_NAME, CONTENT_LINES);

		final String resultFileName = testObj.saveCSVFile(path, PREFIX_FILE, CONTENT_LINES, HEADERS);

		verify(testObj).printCSVFile(Paths.get(path + FILE_NAME), HEADERS, FILE_NAME, CONTENT_LINES);
		assertThat(resultFileName).isEqualTo(FILE_NAME);
	}

	@Test
	void saveCSVFile_shouldEmptyString_whenContentIsNull() {
		final String path = getClass().getResource("").getPath();
		final String resultFileName = testObj.saveCSVFile(path, PREFIX_FILE, null, HEADERS);

		assertThat(resultFileName).isEqualTo(StringUtils.EMPTY);
	}

}
