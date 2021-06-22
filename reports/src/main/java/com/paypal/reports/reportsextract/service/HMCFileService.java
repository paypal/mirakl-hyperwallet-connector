package com.paypal.reports.reportsextract.service;

import java.util.List;

/**
 * Interface that save a CSV into a File
 */
public interface HMCFileService {

	/**
	 * Save a CSV into a File
	 * @param path {@link String} path where file will be saved
	 * @param prefixFileName {@link String} prefix file name for the file generated
	 * @param lines {@link List<String>} data content
	 * @param headers {@link String} comma separated string that will be the header of the
	 * CSV file
	 * @return {@link String} final file name generated
	 */
	String saveCSVFile(String path, String prefixFileName, List<String> lines, String headers);

}
