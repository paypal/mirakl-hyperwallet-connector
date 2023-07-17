package com.paypal.sellers.bankaccountextraction.services.converters.bankaccounttype;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.TransferType;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyInfo;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyRestrictions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletBankAccountTypeResolverImplTest {

	@InjectMocks
	private HyperwalletBankAccountTypeResolverImpl testObj;

	@Mock
	private HyperwalletBankAccountCurrencyRestrictions countryCurrencyConfigurationMock;

	@Test
	void getBankAccountType_shouldReturnFirstBankAccountTypeCandidate() {
		when(countryCurrencyConfigurationMock.getEntriesFor("US", "USD", TransferType.BANK_ACCOUNT))
				.thenReturn(List.of(currencyEntry("ABA", "US", "USD", TransferType.BANK_ACCOUNT),
						currencyEntry("CAD", "US", "USD", TransferType.BANK_ACCOUNT)));

		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCountry("US");
		hyperwalletBankAccount.setTransferMethodCurrency("USD");
		hyperwalletBankAccount.setType(HyperwalletBankAccount.Type.BANK_ACCOUNT);

		final BankAccountType bankAccountType = testObj.getBankAccountType(hyperwalletBankAccount);

		assertThat(bankAccountType).isEqualTo(BankAccountType.ABA);
	}

	@Test
	void getBankAccountType_shouldThrowException_whenThereAreNotBankAccountTypeCandidates() {
		when(countryCurrencyConfigurationMock.getEntriesFor("US", "USD", TransferType.BANK_ACCOUNT))
				.thenReturn(List.of());

		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCountry("US");
		hyperwalletBankAccount.setTransferMethodCurrency("USD");
		hyperwalletBankAccount.setType(HyperwalletBankAccount.Type.BANK_ACCOUNT);

		final Throwable throwable = catchThrowable(() -> testObj.getBankAccountType(hyperwalletBankAccount));
		assertThat(throwable).isInstanceOf(HMCException.class)
				.hasMessage("No bank account type found for country US, currency USD and transfer type BANK_ACCOUNT");
	}

	@NotNull
	private static HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry currencyEntry(
			final String bankAccountType, final String country, final String currency,
			final TransferType transferType) {
		return new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry(bankAccountType, country,
				List.of(new HyperwalletBankAccountCurrencyInfo(country, currency, transferType)));
	}

}
