package com.paypal.observability.miraklapichecks.services;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.observability.miraklapichecks.connectors.MiraklAPIHealthCheckConnector;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.services.converters.MiraklAPIHealthCheckConnectorConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklHealthCheckServiceImplTest {

	@InjectMocks
	private MiraklHealthCheckServiceImpl testObj;

	@Mock
	private MiraklAPIHealthCheckConnector miraklAPIHealthCheckConnectorMock;

	@Mock
	private MiraklAPIHealthCheckConnectorConverter miraklAPIHealthCheckConnectorConverterMock;

	@Mock
	private MiraklVersion miraklVersionMock;

	@Mock
	private MiraklAPICheck miraklAPICheckMock;

	@Test
	void check_ShouldReturnAPICheck_FromResponse_WhenGetMiraklVersionsReturnsAnything() {
		when(miraklAPIHealthCheckConnectorMock.getVersion()).thenReturn(miraklVersionMock);
		when(miraklAPIHealthCheckConnectorConverterMock.from(miraklVersionMock)).thenReturn(miraklAPICheckMock);

		final MiraklAPICheck miraklAPICheck = testObj.check();

		assertThat(miraklAPICheck).isEqualTo(miraklAPICheckMock);
	}

	@Test
	void check_ShouldReturnAPICheck_FromError_WhenGetMiraklVersionsReturnsAnything() {
		final Exception e = new RuntimeException();
		when(miraklAPIHealthCheckConnectorMock.getVersion()).thenThrow(e);
		when(miraklAPIHealthCheckConnectorConverterMock.from(e)).thenReturn(miraklAPICheckMock);

		final MiraklAPICheck miraklAPICheck = testObj.check();

		assertThat(miraklAPICheck).isEqualTo(miraklAPICheckMock);
	}

}
