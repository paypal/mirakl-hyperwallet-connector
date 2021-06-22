package com.paypal.kyc.service.impl;

import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.service.HMCDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of interface {@link HMCDocumentsExtractService}
 */
@Slf4j
@Service
public class HMCDocumentsExtractServiceImpl implements HMCDocumentsExtractService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("java:S3864")
	public <T extends KYCDocumentInfoModel> void cleanUpDocumentsFiles(final List<T> successFullPushedListOfDocuments) {
		log.info("Cleaning up files from disk...");
		//@formatter:off
        Optional.ofNullable(successFullPushedListOfDocuments).orElse(List.of())
                .stream()
                .map(T::getDocuments)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(KYCDocumentModel::getFile)
                .filter(Objects::nonNull)
                .peek(file -> log.info("File selected to be deleted [{}]", file.getAbsolutePath()))
                .forEach(File::delete);
        //@formatter:on
		log.info("Cleaning up done successfully!");
	}

}
