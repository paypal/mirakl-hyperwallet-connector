package com.paypal.reports.controllers;

import com.paypal.reports.infraestructure.configuration.ReportsConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadReportControllerTest {

	private static final String REPO_PATH = "REPO_PATH/";

	private static final String ABSOLUTE_PATH = "/absolute/path";

	@Spy
	@InjectMocks
	private DownloadReportController testObj;

	@Mock
	private Path pathMock, absolutePathMock;

	@Mock
	private ReportsConfig reportsConfigMock;

	private static final String FILE_NAME = "filename.txt";

	private static final byte[] FILE_BYTES = hexStringToByteArray();

	private static MockedStatic<Paths> PATHS_MOCK;

	private static MockedStatic<Files> FILES_MOCK;

	@BeforeEach
	void setUp() {
		when(reportsConfigMock.getRepoPath()).thenReturn(REPO_PATH);
		PATHS_MOCK = mockStatic(Paths.class);
		FILES_MOCK = mockStatic(Files.class);
	}

	@AfterEach
	void deRegisterStaticMocks() {
		PATHS_MOCK.close();
		FILES_MOCK.close();
	}

	@Test
	void getFinancialReport_shouldReturnHttpStatusOK_whenFileIsCorrectlyAllocated() {
		PATHS_MOCK.when(() -> Paths.get(REPO_PATH + FILE_NAME)).thenReturn(pathMock);
		FILES_MOCK.when(() -> Files.readAllBytes(pathMock)).thenReturn(FILE_BYTES);

		final ResponseEntity<Resource> result = this.testObj.getFinancialReport(FILE_NAME);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getFinancialReport_shouldReturnHttpStatusNOT_FOUND_whenFileDoesNotExist() {
		PATHS_MOCK.when(() -> Paths.get(REPO_PATH + FILE_NAME)).thenReturn(pathMock);
		final NoSuchFileException noSuchFileException = new NoSuchFileException("Something bad happened");
		FILES_MOCK.when(() -> Files.readAllBytes(pathMock)).thenThrow(noSuchFileException);
		when(pathMock.toAbsolutePath()).thenReturn(absolutePathMock);
		when(absolutePathMock.toString()).thenReturn(ABSOLUTE_PATH);

		final ResponseEntity<Resource> result = this.testObj.getFinancialReport(FILE_NAME);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void getFinancialReport_shouldReturnHttpStatusBAD_REQUEST_whenFileCannotBeRead() {
		PATHS_MOCK.when(() -> Paths.get(REPO_PATH + FILE_NAME)).thenReturn(pathMock);
		final IOException ioException = new IOException("Something bad happened");
		FILES_MOCK.when(() -> Files.readAllBytes(pathMock)).thenThrow(ioException);

		final ResponseEntity<Resource> result = this.testObj.getFinancialReport(FILE_NAME);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private static byte[] hexStringToByteArray() {
		final String filename_bytes = "filename_bytes";
		final int len = filename_bytes.length();
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(filename_bytes.charAt(i), 16) << 4)
					+ Character.digit(filename_bytes.charAt(i + 1), 16));
		}
		return data;
	}

}
