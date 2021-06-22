package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklToBankAccountModelStrategyFactorySingleTest {

	@Spy
	@InjectMocks
	private MiraklToBankAccountModelStrategyFactorySingle testObj;

	@Mock
	private Strategy<MiraklShop, BankAccountModel> strategyMock;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklPaymentInformation miraklPaymentInformationMock;

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		when(testObj.getStrategies()).thenReturn(Set.of(strategyMock));

		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);

	}

	@Test
	void execute_shouldReturnNullWhenNoPaymentDetailsIsDefined() {

		final var result = testObj.execute(miraklShopMock);

		assertThat(result).isNull();

	}

	@Test
	void execute_shouldcallSuperExecuteMethodWhenPaymentDetailsIsDefined() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklPaymentInformationMock);

		testObj.execute(miraklShopMock);

		verify(testObj).callSuperExecute(miraklShopMock);
	}

}
