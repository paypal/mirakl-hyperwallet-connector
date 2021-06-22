package com.paypal.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.infrastructure.configuration.PublicKeysHyperwalletApiConfig;
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

	@Spy
	@InjectMocks
	private JwkSetController testObj;

	@Mock
	private PublicKeysHyperwalletApiConfig publicKeysHyperwalletApiConfigMock;

	@Mock
	private ObjectMapper objectMapperMock;

	@Mock
	private InputStream publicKeysFromFileMock, errorMessageMock;

	@Mock
	private Resource errorSourceMock;

	private static final String FILENAME = "filename.txt";

	@BeforeEach
	void setUp() {
		this.testObj.errorResource = this.errorSourceMock;
	}

	@Test
	void getPublicKeys_should() throws IOException {
		doReturn(this.publicKeysFromFileMock).when(this.testObj).getPublicKeysFromFile();

		this.testObj.getPublicKeys();
		verify(this.objectMapperMock).readValue(this.publicKeysFromFileMock, JSONObject.class);

	}

	@Test
	void getPublicKeys_shouldSendMessageErrorWhenFileNotFound() throws IOException {
		when(this.publicKeysHyperwalletApiConfigMock.getHmcPublicKeyLocation()).thenReturn(FILENAME);
		final FileNotFoundException fileNotFoundException = new FileNotFoundException("Something bad happened");
		doThrow(fileNotFoundException).when(this.testObj).getPublicKeysFromFile();
		when(this.errorSourceMock.getInputStream()).thenReturn(this.errorMessageMock);

		this.testObj.getPublicKeys();
		verify(this.objectMapperMock).readValue(this.errorMessageMock, JSONObject.class);

	}

}
