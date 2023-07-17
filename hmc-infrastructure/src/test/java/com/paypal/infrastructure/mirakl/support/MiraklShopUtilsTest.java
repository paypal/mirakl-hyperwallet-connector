package com.paypal.infrastructure.mirakl.support;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklShopUtilsTest {

	private static final String PROGRAM = "program";

	private static final String PROGRAM_2 = "program-2";

	private static final String BANKACCOUNT_TOKEN = "bankAccountToken";

	private static final String BANKACCOUNT_TOKEN_2 = "bankAccountToken-2";

	@Test
	void shouldGetAndSet_programAndBankAccountToken() {
		// given
		final MiraklShop miraklShop = new MiraklShop();

		// when
		MiraklShopUtils.setProgram(miraklShop, PROGRAM);
		MiraklShopUtils.setBankAccountToken(miraklShop, BANKACCOUNT_TOKEN);
		final Optional<String> program = MiraklShopUtils.getProgram(miraklShop);
		final Optional<String> bankAccountToken = MiraklShopUtils.getBankAccountToken(miraklShop);

		// then
		assertThat(program).contains(PROGRAM);
		assertThat(bankAccountToken).contains(BANKACCOUNT_TOKEN);
	}

	@Test
	void shouldOverride_programAndBankAccountToken() {
		// given
		final MiraklShop miraklShop = new MiraklShop();

		// when
		MiraklShopUtils.setProgram(miraklShop, PROGRAM);
		MiraklShopUtils.setBankAccountToken(miraklShop, BANKACCOUNT_TOKEN);
		MiraklShopUtils.setProgram(miraklShop, PROGRAM_2);
		MiraklShopUtils.setBankAccountToken(miraklShop, BANKACCOUNT_TOKEN_2);
		final Optional<String> program = MiraklShopUtils.getProgram(miraklShop);
		final Optional<String> bankAccountToken = MiraklShopUtils.getBankAccountToken(miraklShop);

		// then
		assertThat(program).contains(PROGRAM_2);
		assertThat(bankAccountToken).contains(BANKACCOUNT_TOKEN_2);
	}

	@Test
	void shouldDealWithNulls_programAndBankAccountToken() {
		// given
		final MiraklShop miraklShop = new MiraklShop();

		// when
		MiraklShopUtils.setProgram(miraklShop, null);
		MiraklShopUtils.setBankAccountToken(miraklShop, null);
		MiraklShopUtils.setProgram(miraklShop, PROGRAM_2);
		MiraklShopUtils.setBankAccountToken(miraklShop, BANKACCOUNT_TOKEN_2);
		final Optional<String> program = MiraklShopUtils.getProgram(miraklShop);
		final Optional<String> bankAccountToken = MiraklShopUtils.getBankAccountToken(miraklShop);

		// then
		assertThat(program).contains(PROGRAM_2);
		assertThat(bankAccountToken).contains(BANKACCOUNT_TOKEN_2);
	}

}
