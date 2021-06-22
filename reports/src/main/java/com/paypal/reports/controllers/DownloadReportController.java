package com.paypal.reports.controllers;

import com.paypal.reports.infraestructure.configuration.ReportsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/downloads")
public class DownloadReportController {

	private final ReportsConfig reportsConfig;

	public DownloadReportController(final ReportsConfig reportsConfig) {
		this.reportsConfig = reportsConfig;
	}

	@GetMapping("/financial-report/{file_name}")
	public ResponseEntity<Resource> getFinancialReport(@PathVariable("file_name") final String fileName) {
		final Path path = Paths.get(reportsConfig.getRepoPath() + fileName);
		try {
			final ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
					.contentType(MediaType.parseMediaType("application/csv")).body(resource);
		}
		catch (final NoSuchFileException ex) {
			log.error("Financial report file: [{}] not found in path [{}]", fileName, path.toAbsolutePath().toString(),
					ex);
			return ResponseEntity.notFound().build();
		}
		catch (final IOException ex) {
			log.error("There was an error reading: [{}] file", fileName, ex);
			return ResponseEntity.badRequest().build();
		}
	}

}
