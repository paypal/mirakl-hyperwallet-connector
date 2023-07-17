package com.paypal.infrastructure.support.converter;

import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
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
class MyStrategySingleExecutorTest {

	@Spy
	@InjectMocks
	private MyStrategySingleExecutor testObj;

	@Mock
	private Object sourceMock;

	@Mock
	private Strategy<Object, Object> strategyMock;

	@Test
	void execute_whenStrategyIsApplicable_shouldCallToStrategyConvertMethod() {
		when(strategyMock.isApplicable(sourceMock)).thenReturn(Boolean.TRUE);
		doReturn(Set.of(strategyMock)).when(testObj).getStrategies();

		testObj.execute(sourceMock);

		verify(strategyMock).execute(sourceMock);
	}

	@Test
	void execute_whenStrategyIsNotApplicable_shouldNotCallToStrategyConvertMethod() {
		when(strategyMock.isApplicable(sourceMock)).thenReturn(Boolean.FALSE);
		doReturn(Set.of(strategyMock)).when(testObj).getStrategies();

		testObj.execute(sourceMock);

		verify(strategyMock, never()).execute(sourceMock);
	}

	@Test
	void execute_whenNullSetIsReceived_shouldReturnNull() {
		doReturn(null).when(testObj).getStrategies();

		final String result = testObj.execute(sourceMock);

		assertThat(result).isNull();
	}

	@Test
	void execute_whenEmptySetOfStrategies_shouldReturnNull() {
		doReturn(Collections.emptySet()).when(testObj).getStrategies();

		final String result = testObj.execute(sourceMock);

		assertThat(result).isNull();
	}

	private static class MyStrategySingleExecutor extends SingleAbstractStrategyExecutor<Object, String> {

		@Override
		protected Set<Strategy<Object, String>> getStrategies() {
			return null;
		}

	}

}
