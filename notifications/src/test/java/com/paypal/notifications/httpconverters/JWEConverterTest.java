package com.paypal.notifications.httpconverters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.nimbusds.jose.JOSEException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWEConverterTest {

	private static final String UTF_8 = "UTF-8";

	@Spy
	@InjectMocks
	private JWEConverter testObj;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private HyperwalletEncryption hyperwalletEncryptionMock;

	@Mock
	private HttpInputMessage inputMessageMock;

	@Mock
	private InputStream inputStreamMock, decryptedInputMock;

	@Mock
	private Object expectedResultMock, objectMock;

	@Mock
	private HttpOutputMessage outputMessageMock;

	@Mock
	private OutputStream outputStreamMock;

	private byte[] outputByteStub;

	@Test
	void supports_shouldReturnTrueWhenIncomingClassIsHyperwalletWebhookNotification() {
		final boolean result = testObj.supports(HyperwalletWebhookNotification.class);

		assertThat(result).isTrue();
	}

	@Test
	void supports_shouldReturnTrueWhenIncomingClassIsDifferentFromHyperwalletWebhookNotification() {
		final boolean result = testObj.supports(Hyperwallet.class);

		assertThat(result).isFalse();
	}

	@Test
	void readInternal_shouldUseObjectMapperWithTheDecryptedInputStream() throws IOException {
		when(inputMessageMock.getBody()).thenReturn(inputStreamMock);
		doReturn(decryptedInputMock).when(testObj).decrypt(inputStreamMock);
		when(objectMapper.readValue(decryptedInputMock, Object.class)).thenReturn(expectedResultMock);

		final Object result = testObj.readInternal(Object.class, inputMessageMock);

		verify(objectMapper).readValue(decryptedInputMock, Object.class);
		assertThat(result).isEqualTo(expectedResultMock);
	}

	@Test
	void writeInternal_shouldWriteInOutputStreamResultOfObjectMapperWritingValuesAsBytes() throws IOException {
		outputByteStub = "anyString".getBytes();
		when(outputMessageMock.getBody()).thenReturn(outputStreamMock);
		when(objectMapper.writeValueAsBytes(objectMock)).thenReturn(outputByteStub);

		testObj.writeInternal(objectMock, outputMessageMock);

		verify(outputStreamMock).write(outputByteStub);
	}

	@Test
	void getBodyAsString_shouldReturnCorrespondentStringFromInputStream() throws IOException {
		final InputStream inputStream = IOUtils.toInputStream("This is the input stream", UTF_8);

		final String result = testObj.getBodyAsString(inputStream);

		assertThat(result).isEqualTo("This is the input stream");
	}

	@Test
	void decrypt_shouldDecryptTheBodyOfInputStreamUsingHyperwalletEncryptionClass()
			throws ParseException, JOSEException, IOException {
		final String encryptedBody = "encryptedBody";
		final String decryptedBody = "decryptedBody";
		doReturn(encryptedBody).when(testObj).getBodyAsString(inputStreamMock);
		when(hyperwalletEncryptionMock.decrypt(encryptedBody)).thenReturn(decryptedBody);

		final InputStream result = testObj.decrypt(inputStreamMock);

		assertThat(IOUtils.toString(result, StandardCharsets.UTF_8)).isEqualTo(decryptedBody);
	}

}
