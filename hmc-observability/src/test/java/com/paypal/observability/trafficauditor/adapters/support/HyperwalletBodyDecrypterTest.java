package com.paypal.observability.trafficauditor.adapters.support;

import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletBodyDecrypterTest {

	@InjectMocks
	private HyperwalletBodyDecrypter testObj;

	@Mock
	private HyperwalletEncryption hyperwalletEncryptionMock;

	@Test
	void decryptBodyIfNeeded_shouldReturnBody_ifBodyIsNull() {
		// given
		final String body = null;

		// when
		final String result = testObj.decryptBodyIfNeeded(body);

		// then
		assertThat(result).isNull();
	}

	@Test
	void decryptBodyIfNeeded_shouldReturnBody_ifBodyIsBlank() {
		// given
		final String body = "";

		// when
		final String result = testObj.decryptBodyIfNeeded(body);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void decryptBodyIfNeeded_shouldReturnBody_ifHyperwalletEncryptionIsNull() {
		// given
		final String body = "body";
		testObj = new HyperwalletBodyDecrypter(null);

		// when
		final String result = testObj.decryptBodyIfNeeded(body);

		// then
		assertThat(result).isEqualTo("body");
	}

	@Test
	void decryptBodyIfNeeded_shouldReturnBody_ifBodyIsNotEncrypted() throws Exception {
		// given
		final String body = "body";
		doThrow(RuntimeException.class).when(hyperwalletEncryptionMock).decrypt(body);

		// when
		final String result = testObj.decryptBodyIfNeeded(body);

		// then
		assertThat(result).isEqualTo("body");
	}

	@Test
	void decryptBodyIfNeeded_shouldReturnDecryptedBody_ifBodyIsEncrypted() throws Exception {
		// given
		final String body = "ENCRYPTED";
		testObj = new HyperwalletBodyDecrypter(hyperwalletEncryptionMock);
		when(hyperwalletEncryptionMock.decrypt("ENCRYPTED")).thenReturn("DECRYPTED");

		// when
		final String result = testObj.decryptBodyIfNeeded(body);

		// then
		assertThat(result).isEqualTo("DECRYPTED");
	}

}
