package com.paypal.sellers.sellersextract.service.strategies;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletBusinessStakeHolderServiceExecutorSingleTest {

	@InjectMocks
	private HyperWalletBusinessStakeHolderServiceExecutor testObj;

	@Mock
	private Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new HyperWalletBusinessStakeHolderServiceExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
