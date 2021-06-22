package com.paypal.infrastructure.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MultipleAbstractStrategyFactoryTest {

	@Spy
	@InjectMocks
	private MyMultipleAbstractStrategyFactory testObj;

	@Mock
	private Object sourceMock;

	@Mock
	private Strategy<Object, Object> strategyOneMock, strategyTwoMock;

	@Test
	void execute_shouldCalltoStategyConvertMethod_whenStrategyIsApplicable() {
		doReturn(Set.of(strategyOneMock)).when(testObj).getStrategies();
		when(strategyOneMock.isApplicable(sourceMock)).thenReturn(Boolean.TRUE);

		testObj.execute(sourceMock);

		verify(strategyOneMock).execute(sourceMock);

	}

	@Test
	void execute_shouldReturnEmptyList_whenNullSetIsReceived() {
		doReturn(null).when(testObj).getStrategies();

		final var result = testObj.execute(sourceMock);

		assertThat(result).isEmpty();
	}

	@Test
	void execute_shouldReturnEmptyList_whenEmptySetOfStrategies() {
		doReturn(Collections.emptySet()).when(testObj).getStrategies();

		final var result = testObj.execute(sourceMock);

		assertThat(result).isEmpty();

	}

	@Test
	void execute_shouldExecuteTwoStrategies_whenSomeStrategiesAreApplicable() {
		when(strategyOneMock.isApplicable(sourceMock)).thenReturn(Boolean.TRUE);
		when(strategyTwoMock.isApplicable(sourceMock)).thenReturn(Boolean.TRUE);
		doReturn(Set.of(strategyOneMock, strategyTwoMock)).when(testObj).getStrategies();

		testObj.execute(sourceMock);

		verify(strategyOneMock).execute(sourceMock);
		verify(strategyTwoMock).execute(sourceMock);

	}

	private static class MyMultipleAbstractStrategyFactory extends MultipleAbstractStrategyFactory<Object, String> {

		@Override
		protected Set<Strategy<Object, String>> getStrategies() {
			return null;
		}

	}

}
