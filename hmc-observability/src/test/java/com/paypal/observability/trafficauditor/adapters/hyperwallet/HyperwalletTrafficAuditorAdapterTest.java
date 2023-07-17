package com.paypal.observability.trafficauditor.adapters.hyperwallet;

import com.hyperwallet.clientsdk.util.Request;
import com.paypal.observability.trafficauditor.adapters.TrafficAuditorTraceHolder;
import com.paypal.observability.trafficauditor.adapters.support.HyperwalletBodyDecrypter;
import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletTrafficAuditorAdapterTest {

	private HyperwalletTrafficAuditorAdapter testObj;

	@Mock
	private TrafficAuditorConfiguration trafficAuditorConfigurationMock;

	@Mock
	private TrafficAuditorTraceHolder trafficAuditorTraceHolderMock;

	@Mock
	private HyperwalletBodyDecrypter hyperwalletBodyDecrypterMock;

	@Test
	void doCaptureRequest_shouldCallDecryptBody() {
		// given
		testObj = new HyperwalletTrafficAuditorAdapter(trafficAuditorConfigurationMock, trafficAuditorTraceHolderMock,
				hyperwalletBodyDecrypterMock);

		final Request request = new Request("https://test.com", 50000, 50000, null, null, null);
		request.setHeaders(Map.of("Content-Type", List.of("application/jose+json")));
		request.setBody("ENCRYPTED");
		when(hyperwalletBodyDecrypterMock.decryptBodyIfNeeded("ENCRYPTED")).thenReturn("DECRYPTED");

		// when
		final TrafficAuditorRequest result = testObj.doCaptureRequest(request);

		// then
		verify(hyperwalletBodyDecrypterMock).decryptBodyIfNeeded("ENCRYPTED");
		assertThat(result.getBody()).isEqualTo("DECRYPTED");
	}

}
