package com.paypal.infrastructure.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultipleAbstractStrategyExecutorTest {

	@Spy
	@InjectMocks
	private MyMultipleAbstractStrategyExecutor testObj;

	@Mock
	private Object sourceMock;

	@Mock
	private Strategy<Object, Object> strategyOneMock, strategyTwoMock;

	@Test
	void execute_whenStrategyIsApplicable_shouldCallToStrategyConvertMethod() {
		when(strategyOneMock.isApplicable(sourceMock)).thenReturn(Boolean.TRUE);
		doReturn(Set.of(strategyOneMock)).when(testObj).getStrategies();

		testObj.execute(sourceMock);

		verify(strategyOneMock).execute(sourceMock);
	}

	@Test
	void execute_whenNullSetIsReceived_shouldReturnEmptyList() {
		doReturn(null).when(testObj).getStrategies();

		final List<String> result = testObj.execute(sourceMock);

		assertThat(result).isEmpty();
	}

	@Test
	void execute_whenEmptySetOfStrategies_shouldReturnEmptyList() {
		doReturn(Collections.emptySet()).when(testObj).getStrategies();

		final List<String> result = testObj.execute(sourceMock);

		assertThat(result).isEmpty();
	}

	@Test
	void execute_whenSomeStrategiesAreApplicable_shouldExecuteTwoStrategies() {
		when(strategyOneMock.isApplicable(sourceMock)).thenReturn(true);
		when(strategyTwoMock.isApplicable(sourceMock)).thenReturn(true);
		doReturn(Set.of(strategyOneMock, strategyTwoMock)).when(testObj).getStrategies();

		testObj.execute(sourceMock);

		verify(strategyOneMock).execute(sourceMock);
		verify(strategyTwoMock).execute(sourceMock);
	}

	@Test
	void execute_whenNoStrategiesAreApplicable_shouldNotExecuteStrategies() {
		when(strategyOneMock.isApplicable(sourceMock)).thenReturn(false);
		when(strategyTwoMock.isApplicable(sourceMock)).thenReturn(false);
		doReturn(Set.of(strategyOneMock, strategyTwoMock)).when(testObj).getStrategies();

		testObj.execute(sourceMock);

		verify(strategyOneMock, times(0)).execute(sourceMock);
		verify(strategyTwoMock, times(0)).execute(sourceMock);
	}

	private static class MyMultipleAbstractStrategyExecutor extends MultipleAbstractStrategyExecutor<Object, String> {

		@Override
		protected Set<Strategy<Object, String>> getStrategies() {
			return null;
		}

	}

}
