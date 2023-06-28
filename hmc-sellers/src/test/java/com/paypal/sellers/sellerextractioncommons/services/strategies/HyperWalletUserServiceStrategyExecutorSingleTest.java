package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.strategies.HyperWalletUserServiceStrategyExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletUserServiceStrategyExecutorSingleTest {

	@InjectMocks
	private HyperWalletUserServiceStrategyExecutor testObj;

	@Mock
	private Strategy<SellerModel, SellerModel> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new HyperWalletUserServiceStrategyExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<SellerModel, SellerModel>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
