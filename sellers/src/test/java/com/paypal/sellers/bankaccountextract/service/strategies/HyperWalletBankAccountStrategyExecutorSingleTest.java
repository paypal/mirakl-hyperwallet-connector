package com.paypal.sellers.bankaccountextract.service.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletBankAccountStrategyExecutorSingleTest {

	@Spy
	@InjectMocks
	private HyperWalletBankAccountStrategyExecutor testObj;

	@Mock
	private Strategy<SellerModel, Optional<HyperwalletBankAccount>> strategyMock;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private BankAccountModel bankAccountModelMock;

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		testObj = new HyperWalletBankAccountStrategyExecutor(Set.of(strategyMock));
		final Set<Strategy<SellerModel, Optional<HyperwalletBankAccount>>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

	@Test
	void execute_shouldReturnEmptyWhenEmptyBankDetailsIsReceived() {
		testObj = new HyperWalletBankAccountStrategyExecutor(Set.of(strategyMock));
		when(sellerModelMock.getBankAccountDetails()).thenReturn(null);

		final Optional<HyperwalletBankAccount> result = testObj.execute(sellerModelMock);

		assertThat(result).isNotPresent();
	}

	@Test
	void execute_shouldCallExecuteWhenBankDetailsAreNotEmpty() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(bankAccountModelMock);

		testObj.execute(sellerModelMock);

		verify(testObj).callSuperExecute(sellerModelMock);
	}

}
