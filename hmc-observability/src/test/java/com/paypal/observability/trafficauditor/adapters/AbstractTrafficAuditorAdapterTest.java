package com.paypal.observability.trafficauditor.adapters;

import com.paypal.observability.trafficauditor.configuration.TrafficAuditorConfiguration;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTarget;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractTrafficAuditorAdapterTest {

	@InjectMocks
	private MyTrafficAuditorAdapter testObj;

	@Mock
	private TrafficAuditorConfiguration trafficAuditorConfigurationMock;

	@Mock
	private TrafficAuditorTraceHolder trafficAuditorTraceHolderMock;

	@Test
	void captureMethods_WhenTrafficAuditorDisabled_ShouldDoNothing() {
		// given
		when(trafficAuditorConfigurationMock.isTrafficAuditorEnabled()).thenReturn(false);

		// when
		testObj.startTraceCapture(mock(Object.class));
		testObj.endTraceCapture(mock(Object.class));
		testObj.endTraceCapture(mock(Throwable.class));

		// then
		verify(trafficAuditorTraceHolderMock, never()).request(any());
		verify(trafficAuditorTraceHolderMock, never()).response(any());
		verify(trafficAuditorTraceHolderMock, never()).noResponse();
	}

	@Test
	void captureMethods_WhenTrafficAuditorDisabled_ShouldDoTraceCapture() {
		// given
		when(trafficAuditorConfigurationMock.isTrafficAuditorEnabled()).thenReturn(true);
		final TrafficAuditorTrace trafficAuditorTrace = new TrafficAuditorTrace();
		trafficAuditorTrace.setRequest(testObj.trafficAuditorRequest);
		when(trafficAuditorTraceHolderMock.currentTrace()).thenReturn(trafficAuditorTrace);

		// when
		testObj.startTraceCapture(testObj.trafficAuditorRequest);
		testObj.endTraceCapture(testObj.trafficAuditorResponse);

		// then
		verify(trafficAuditorTraceHolderMock, times(1)).request(any());
		verify(trafficAuditorTraceHolderMock, times(1)).response(any());
	}

	@Test
	void captureRequest_shouldIgnoreRequest_whenItsMultipart() {
		// given
		when(trafficAuditorConfigurationMock.isTrafficAuditorEnabled()).thenReturn(true);
		final TrafficAuditorTrace trafficAuditorTrace = new TrafficAuditorTrace();
		trafficAuditorTrace.setRequest(testObj.trafficAuditorRequest);
		testObj.trafficAuditorResponse.getHeaders().put("Content-Type", List.of("multipart/form-data"));
		when(trafficAuditorTraceHolderMock.currentTrace()).thenReturn(trafficAuditorTrace);

		// when
		testObj.startTraceCapture(testObj.trafficAuditorRequest);
		testObj.endTraceCapture(testObj.trafficAuditorResponse);

		// then
		verify(trafficAuditorTraceHolderMock, times(1)).request(any());
		verify(trafficAuditorTraceHolderMock, never()).response(any());
	}

	@Test
	void captureResponse_shouldIgnoreResponse_whenItsMultipart() {
		// given
		when(trafficAuditorConfigurationMock.isTrafficAuditorEnabled()).thenReturn(true);
		testObj.trafficAuditorRequest.getHeaders().put("Content-Type", List.of("multipart/form-data"));

		// when
		testObj.startTraceCapture(testObj.trafficAuditorRequest);

		// then
		verify(trafficAuditorTraceHolderMock, never()).request(any());
	}

	static class MyTrafficAuditorAdapter extends AbstractTrafficAuditorAdapter<Object, Object> {

		private static TrafficAuditorAdapter<?, ?> instance;
		final TrafficAuditorRequest trafficAuditorRequest;

		final TrafficAuditorResponse trafficAuditorResponse;

		protected MyTrafficAuditorAdapter(final TrafficAuditorConfiguration trafficAuditorConfiguration,
				final TrafficAuditorTraceHolder trafficAuditorTraceHolder) {
			super(trafficAuditorConfiguration, trafficAuditorTraceHolder);
			trafficAuditorRequest = buildRequest();
			trafficAuditorResponse = buildResponse();
		}

		@SuppressWarnings("unchecked")
		public static <T, R> TrafficAuditorAdapter<T, R> get() {
			return (TrafficAuditorAdapter<T, R>) instance;
		}

		@Override
		protected TrafficAuditorRequest doCaptureRequest(final Object request) {
			return trafficAuditorRequest;
		}

		@Override
		protected TrafficAuditorResponse doCaptureResponse(final Object response) {
			return trafficAuditorResponse;
		}

		@Override
		protected TrafficAuditorTarget getTarget() {
			return TrafficAuditorTarget.HMC;
		}

		TrafficAuditorRequest buildRequest() {
			final TrafficAuditorRequest request = new TrafficAuditorRequest();
			request.setBody("REQUEST BODY");
			request.setMethod("GET");
			request.setUrl("http://localhost:8080/something");
			request.setQueryParameters(Map.of("param1", "value1", "param2", "value2"));
			request.setHeaders(new HashMap<>(Map.of("header1", List.of("value1"), "header2", List.of("value2"))));

			return request;
		}

		TrafficAuditorResponse buildResponse() {
			final TrafficAuditorResponse response = new TrafficAuditorResponse();
			response.setResponseCode(200);
			response.setBody("RESPONSE BODY");
			response.setHeaders(new HashMap<>(Map.of("header1", List.of("value1"), "header2", List.of("value2"))));

			return response;
		}

		@SuppressWarnings("java:S2696")
		@Override
		public void afterPropertiesSet() {
			instance = this;
		}
	}

}
