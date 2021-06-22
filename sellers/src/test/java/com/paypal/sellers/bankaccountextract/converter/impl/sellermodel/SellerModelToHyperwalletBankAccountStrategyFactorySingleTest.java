package com.paypal.sellers.bankaccountextract.converter.impl.sellermodel;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
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
class SellerModelToHyperwalletBankAccountStrategyFactorySingleTest {

	@InjectMocks
	private SellerModelToHyperwalletBankAccountStrategyFactorySingle testObj;

	@Mock
	private Strategy<SellerModel, HyperwalletBankAccount> strategy1, strategy2;

	@BeforeEach
	void setUp() {
		testObj = new SellerModelToHyperwalletBankAccountStrategyFactorySingle(Set.of(strategy1, strategy2));
	}

	@Test
	void getStrategies_shouldReturnSetOfAvailableStrategies() {

		final var result = testObj.getStrategies();

		assertThat(result).containsExactlyInAnyOrder(strategy1, strategy2);

	}

}
