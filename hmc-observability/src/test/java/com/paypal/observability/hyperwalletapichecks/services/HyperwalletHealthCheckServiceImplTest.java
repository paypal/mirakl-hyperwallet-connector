package com.paypal.observability.hyperwalletapichecks.services;

import com.hyperwallet.clientsdk.model.HyperwalletProgram;
import com.paypal.observability.hyperwalletapichecks.connectors.HyperwalletAPIHealthCheckConnector;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.services.converters.HyperwalletAPIHealthCheckConnectorConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletHealthCheckServiceImplTest {

	@InjectMocks
	private HyperwalletHealthCheckServiceImpl testObj;

	@Mock
	private HyperwalletAPIHealthCheckConnector hyperwalletAPIHealthCheckConnectorMock;

	@Mock
	private HyperwalletAPIHealthCheckConnectorConverter hyperwalletAPIHealthCheckConnectorConverterMock;

	@Mock
	private HyperwalletProgram hyperwalletProgramMock;

	@Mock
	private HyperwalletAPICheck hyperwalletAPICheckMock;

	@Test
	void check_ShouldReturnAPICheck_FromResponse_WhenGetHyperwalletProgramReturnsAnything() {
		when(hyperwalletAPIHealthCheckConnectorMock.getProgram()).thenReturn(hyperwalletProgramMock);
		when(hyperwalletAPIHealthCheckConnectorConverterMock.from(hyperwalletProgramMock))
				.thenReturn(hyperwalletAPICheckMock);

		final HyperwalletAPICheck hyperwalletAPICheck = testObj.check();

		assertThat(hyperwalletAPICheck).isEqualTo(hyperwalletAPICheckMock);
	}

	@Test
	void check_ShouldReturnAPICheck_FromError_WhenGetHyperwalletProgramReturnsAnything() {
		final Exception e = new RuntimeException();
		when(hyperwalletAPIHealthCheckConnectorMock.getProgram()).thenThrow(e);
		when(hyperwalletAPIHealthCheckConnectorConverterMock.from(e)).thenReturn(hyperwalletAPICheckMock);

		final HyperwalletAPICheck hyperwalletAPICheck = testObj.check();

		assertThat(hyperwalletAPICheck).isEqualTo(hyperwalletAPICheckMock);
	}

}
