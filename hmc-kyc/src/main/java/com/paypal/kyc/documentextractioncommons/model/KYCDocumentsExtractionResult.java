package com.paypal.kyc.documentextractioncommons.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class holds the results of a document extraction, storing the extracted documents
 * along some error information related to the extraction process.
 *
 * @param <T> The type of {@link KYCDocumentInfoModel}, so this class can be used both for
 * Seller and BusinessStakeholder documents.
 */
public class KYCDocumentsExtractionResult<T extends KYCDocumentInfoModel> {

	private int numberOfFailures = 0;

	private final List<T> kycDocumentInfoModels = new ArrayList<>();

	/**
	 * Adds a new document to the extraction process result. It receives a method that
	 * should return the document to be added. If an exception is thrown by the method it
	 * will increase the internal count of failures in the extraction process.
	 * @param documentSupplier a method returning the extracted document to be added to
	 * the result.
	 */
	public void addDocument(final Supplier<T> documentSupplier) {
		try {
			kycDocumentInfoModels.add(documentSupplier.get());
		}
		catch (final Exception e) {
			numberOfFailures++;
		}
	}

	/**
	 * Returns the list of extracted documents.
	 * @return a list of extracted documents.
	 */
	public List<T> getExtractedDocuments() {
		return kycDocumentInfoModels;
	}

	/**
	 * Checks if there has been errors during the extraction process.
	 * @return a boolean indicating if any error was captured.
	 */
	public boolean hasFailed() {
		return numberOfFailures != 0;
	}

	/**
	 * Return the number of errors captured during the extraction process.
	 * @return the number of errors captured during the extraction process.
	 */
	public int getNumberOfFailures() {
		return numberOfFailures;
	}

}
