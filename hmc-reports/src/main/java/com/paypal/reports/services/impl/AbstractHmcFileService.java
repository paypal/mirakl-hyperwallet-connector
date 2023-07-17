package com.paypal.reports.services.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Abstract HMC File Service that manages the way to print a CSV File (with RFC4180
 * format) and the way to save it into a certain path.
 */

@Slf4j
@Data
public abstract class AbstractHmcFileService {

	/**
	 * Print and save a CSV File into a certain path
	 * @param filePath {@link Path} where storing the CSV file
	 * @param headers {@link String}of CSV
	 * @param fileName {@link String} prefix fileName
	 * @param lines {@link List<String>} contains the information to be printed into CSV
	 * format
	 * @return {@link String} result filename
	 * @throws IOException when file could not be written into path received as parameter
	 */

	protected String printCSVFile(final Path filePath, final String headers, final String fileName,
			final List<String> lines) throws IOException {
		CSVPrinter csvPrinter = null;
		try {
			csvPrinter = getCSVPrinter(filePath, headers, fileName);
			if (Objects.nonNull(csvPrinter)) {
				csvPrinter.printRecords(lines);
				csvPrinter.flush();
				return fileName;
			}
		}
		catch (final IOException e) {
			log.error("CSV File [{}] could not be written into path: [{}]", fileName, filePath.toAbsolutePath(), e);
		}
		finally {
			if (Objects.nonNull(csvPrinter)) {
				csvPrinter.close();
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Creates a CSVPrinter instance according to parameters received
	 * @param path {@link Path} where the CSVPrinter points to
	 * @param headers {@link String} contains the header for the CSV
	 * @param fileName {@link String} prefix name for the file created
	 * @return {@link CSVPrinter} object
	 */
	@SuppressWarnings("java:S2095")
	protected CSVPrinter getCSVPrinter(final Path path, final String headers, final String fileName) {
		try {
			final FileWriter writer = new FileWriter(path.toString());
			return new CSVPrinter(writer, CSVFormat.RFC4180.withHeader(headers).withQuote(null));
		}
		catch (final IOException e) {
			log.error("CSV File [{}] could not be written into path: [{}]", fileName, path, e);
			return null;
		}
	}

}
