package com.paypal.infrastructure.converter;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingleAbstractStrategyFactoryTest {

	@Spy
	@InjectMocks
	private MyStrategyFactorySingle testObj;

	@Mock
	private Object sourceMock;

	@Mock
	private Strategy<Object, Object> strategyMock;

	@Test
	void execute_shouldCalltoStategyConvertMethod_whenStrategyIsApplicable() {
		doReturn(Set.of(strategyMock)).when(testObj).getStrategies();
		when(strategyMock.isApplicable(sourceMock)).thenReturn(Boolean.TRUE);

		testObj.execute(sourceMock);

		verify(strategyMock).execute(sourceMock);

	}

	@Test
	void execute_shouldReturnNull_whenNullSetIsReceived() {
		doReturn(null).when(testObj).getStrategies();

		final var result = testObj.execute(sourceMock);

		assertThat(result).isNull();
	}

	@Test
	void execute_shouldReturnNull_whenEmptySetOfStrategies() {
		doReturn(Collections.emptySet()).when(testObj).getStrategies();

		final var result = testObj.execute(sourceMock);

		assertThat(result).isNull();

	}

	private static class MyStrategyFactorySingle extends SingleAbstractStrategyFactory<Object, String> {

		@Override
		protected Set<Strategy<Object, String>> getStrategies() {
			return null;
		}

	}

}
