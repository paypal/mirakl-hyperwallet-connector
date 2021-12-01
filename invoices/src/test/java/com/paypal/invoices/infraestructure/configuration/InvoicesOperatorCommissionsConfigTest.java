package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoicesOperatorCommissionsConfigTest {

	private static final String BANK_ACCOUNT_TOKENS_PREFIX = "invoices.operator.commissions.bankAccount.token.";

	@Spy
	@InjectMocks
	private InvoicesOperatorCommissionsConfig testObj;

	@Test
	void loadBankAccountTokensMapConfiguration_shouldCallSuperLoad() throws InvalidConfigurationException {
		testObj.loadBankAccountTokensMapConfiguration();

		verify(testObj).callSuperLoad(BANK_ACCOUNT_TOKENS_PREFIX);
	}

	@Test
	void getBankAccountToken_shouldReturnNullWhenNoTokenIsDefined() {
		final String result = testObj.getBankAccountToken("invalidToken");

		assertThat(result).isNull();
	}

	@Test
	void getBankAccountToken_shouldReturnTokenValueIfTokenKeyIsDefined() {
		testObj.setBankAccountTokens(Map.of("tokenKey", "tokenValue"));
		final String result = testObj.getBankAccountToken("tokenKey");

		assertThat(result).isEqualTo("tokenValue");
	}

	@Test
	void getBankAccountToken_shouldReturnNullIfTokenKeyIsNoyDefined() {
		testObj.setBankAccountTokens(Map.of("tokenKey", "tokenValue"));
		final String result = testObj.getBankAccountToken("invalidTokenKey");

		assertThat(result).isNull();
	}

	@Test
	void getBankAccountToken_shouldReturnNullWhenNullParameterIsReceived() {
		testObj.setBankAccountTokens(Map.of("tokenKey", "tokenValue"));

		final String result = testObj.getBankAccountToken(null);

		assertThat(result).isEqualTo(StringUtils.EMPTY);
	}

	@Test
	void getBankAccountToken_shouldReturnNullWhenEmptyStringIsReceived() {
		testObj.setBankAccountTokens(Map.of("tokenKey", "tokenValue"));

		final String result = testObj.getBankAccountToken("");

		assertThat(result).isEqualTo(StringUtils.EMPTY);
	}

}
