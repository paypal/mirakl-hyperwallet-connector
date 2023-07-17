package com.paypal.reports.services.impl;

import com.paypal.infrastructure.support.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HMCFileServiceImplTest {

	private static final String PREFIX_FILE = "prefixFile";

	private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm";

	private static final String DATE = "2020-11-10_20-45";

	private static final String FILE_NAME = "prefixFile_2020-11-10_20-45.csv";

	private static final List<String> CONTENT_LINES = List.of("column11,column12,column13",
			"column21,column22,column23");

	private static final String HEADERS = "headerColumn1,headerColumn2,headerColumn3";

	@Spy
	@InjectMocks
	private HMCFileServiceImpl testObj;

	private static MockedStatic<DateUtil> DATEUTIL_MOCK;

	private static MockedStatic<LocalDateTime> LOCALDATETIME_MOCK;

	private static LocalDateTime NOW;

	@BeforeEach
	void setUp() {
		NOW = LocalDateTime.of(2020, 11, 10, 20, 45);
		DATEUTIL_MOCK = mockStatic(DateUtil.class);
		LOCALDATETIME_MOCK = mockStatic(LocalDateTime.class);
	}

	@AfterEach
	void deRegisterStaticMocks() {
		DATEUTIL_MOCK.close();
		LOCALDATETIME_MOCK.close();
	}

	@Test
	void saveCSVFile_shouldSaveCSVFile_whenPathAndContentAreNotNull() throws IOException {
		final String path = getClass().getResource("").getPath();
		doReturn(FILE_NAME).when(testObj).printCSVFile(Paths.get(path + FILE_NAME), HEADERS, FILE_NAME, CONTENT_LINES);
		LOCALDATETIME_MOCK.when(LocalDateTime::now).thenReturn(NOW);
		DATEUTIL_MOCK.when(() -> DateUtil.convertToString(NOW, DATE_FORMAT)).thenReturn(DATE);

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
