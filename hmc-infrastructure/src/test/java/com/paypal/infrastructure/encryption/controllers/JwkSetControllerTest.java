package com.paypal.infrastructure.encryption.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletEncryptionConfiguration;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwkSetControllerTest {

	private static final String FILENAME = "filename.txt";

	@Spy
	@InjectMocks
	private JwkSetController testObj;

	@Mock
	private HyperwalletEncryptionConfiguration hyperwalletEncryptionConfigurationMock;

	@Mock
	private Resource errorSourceMock;

	@Mock
	private ObjectMapper objectMapperMock;

	@Mock
	private InputStream publicKeysFromFileMock, errorMessageMock;

	@BeforeEach
	void setUp() {
		testObj.errorResource = errorSourceMock;
	}

	@Test
	void getPublicKeys_should() throws IOException {
		doReturn(publicKeysFromFileMock).when(testObj).getPublicKeysFromFile();

		testObj.getPublicKeys();

		verify(objectMapperMock).readValue(publicKeysFromFileMock, JSONObject.class);
	}

	@Test
	void getPublicKeys_shouldSendMessageErrorWhenFileNotFound() throws IOException {
		when(hyperwalletEncryptionConfigurationMock.getHmcPublicKeyLocation()).thenReturn(FILENAME);
		final FileNotFoundException fileNotFoundException = new FileNotFoundException("Something bad happened");
		doThrow(fileNotFoundException).when(testObj).getPublicKeysFromFile();
		when(errorSourceMock.getInputStream()).thenReturn(errorMessageMock);

		testObj.getPublicKeys();

		verify(objectMapperMock).readValue(errorMessageMock, JSONObject.class);
	}

}
