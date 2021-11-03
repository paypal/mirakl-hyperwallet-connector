package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklShopToSellerConverterStrategyExecutorTest {

	@InjectMocks
	private MiraklShopToSellerConverterStrategyExecutor testObj;

	@Mock
	private Strategy<MiraklShop, SellerModel> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new MiraklShopToSellerConverterStrategyExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {

		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);

	}

}
