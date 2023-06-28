package com.paypal.sellers.stakeholdersextraction.services.strategies;

import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletBusinessStakeHolderStrategyExecutorSingleTest {

	@InjectMocks
	private HyperWalletBusinessStakeHolderStrategyExecutor testObj;

	@Mock
	private Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new HyperWalletBusinessStakeHolderStrategyExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
