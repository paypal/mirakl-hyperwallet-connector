package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklAbaBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklUkBankAccountInformation;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;
import com.paypal.sellers.bankaccountextract.model.TransferType;
import com.paypal.sellers.sellersextract.model.SellerModelConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.sellers.sellersextract.model.SellerModelConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklShopToUKBankAccountModelConverterStrategyTest {

	private static final String FIRST_NAME = "firstName";

	private static final String LAST_NAME = "lastName";

	private static final String STREET_1 = "street1";

	private static final String STREET_2 = "street2";

	private static final String CITY_NAME = "city";

	private static final String GB_COUNTRY = "GBR";

	private static final String BUSINESS_NAME = "business_name";

	private static final String UK_COUNTRY_ISO = "GB";

	private static final String GBP_CURRENCY = "GBP";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String TOKEN = "bankAccountToken";

	private static final String STATE = "bankAccountState";

	private static final String BANK_ACCOUNT_NUMBER = "bankAccountNumber";

	private static final String SORT_CODE = "sortCode";

	@InjectMocks
	private MiraklShopToUKBankAccountModelConverterStrategy testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklContactInformation contactInformationMock;

	@Mock
	private MiraklUkBankAccountInformation miraklUKBankAccountInformationMock;

	@Mock
	private MiraklProfessionalInformation miraklProfessionalInformationMock;

	@Mock
	private MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue miraklBankAccountTokenFieldValueMock,
			miraklBankAccountStateFieldValueMock;

	@Mock
	private MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue miraklHyperwalletProgramFieldValueMock;

	@Mock
	private MiraklAbaBankAccountInformation miraklABABankAccountInformationMock;

	@Test
	void convert_ShouldTransformFromMiraklShopToUkBankAccountModel() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklUKBankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.GBP);
		when(miraklShopMock.getProfessionalInformation()).thenReturn(miraklProfessionalInformationMock);
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(List.of(miraklBankAccountTokenFieldValueMock,
				miraklBankAccountStateFieldValueMock, miraklHyperwalletProgramFieldValueMock));
		when(miraklBankAccountTokenFieldValueMock.getCode()).thenReturn(HYPERWALLET_BANK_ACCOUNT_TOKEN);
		when(miraklBankAccountTokenFieldValueMock.getValue()).thenReturn(TOKEN);
		when(miraklBankAccountStateFieldValueMock.getCode()).thenReturn(HYPERWALLET_BANK_ACCOUNT_STATE);
		when(miraklBankAccountStateFieldValueMock.getValue()).thenReturn(STATE);
		when(miraklHyperwalletProgramFieldValueMock.getCode()).thenReturn(SellerModelConstants.HYPERWALLET_PROGRAM);
		when(miraklHyperwalletProgramFieldValueMock.getValue()).thenReturn(HYPERWALLET_PROGRAM);

		when(contactInformationMock.getFirstname()).thenReturn(FIRST_NAME);
		when(contactInformationMock.getLastname()).thenReturn(LAST_NAME);
		when(contactInformationMock.getStreet1()).thenReturn(STREET_1);
		when(contactInformationMock.getStreet2()).thenReturn(STREET_2);
		when(miraklUKBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);
		when(contactInformationMock.getCountry()).thenReturn(GB_COUNTRY);

		when(miraklUKBankAccountInformationMock.getBankAccountNumber()).thenReturn(BANK_ACCOUNT_NUMBER);
		when(miraklUKBankAccountInformationMock.getBankSortCode()).thenReturn(SORT_CODE);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final var result = testObj.execute(miraklShopMock);
		//@formatter:off
        assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", UK_COUNTRY_ISO)
                .hasFieldOrPropertyWithValue("transferMethodCurrency", GBP_CURRENCY)
                .hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
                .hasFieldOrPropertyWithValue("type", BankAccountType.UK)
                .hasFieldOrPropertyWithValue("bankAccountId", SORT_CODE)
                .hasFieldOrPropertyWithValue("bankAccountNumber", BANK_ACCOUNT_NUMBER)
                .hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
                .hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
                .hasFieldOrPropertyWithValue("lastName", LAST_NAME)
                .hasFieldOrPropertyWithValue("country", UK_COUNTRY_ISO)
                .hasFieldOrPropertyWithValue("addressLine1", STREET_1)
                .hasFieldOrPropertyWithValue("addressLine2", STREET_2)
                .hasFieldOrPropertyWithValue("city", CITY_NAME)
                .hasFieldOrPropertyWithValue("stateProvince", STATE)
                .hasFieldOrPropertyWithValue("token", TOKEN)
                .hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
        //@formatter:on
	}

	@Test
	void convert_shouldEnsureThatOptionalFieldLineAddress2IsFilledWithAnEmptyString() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklUKBankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.GBP);
		when(miraklShopMock.getProfessionalInformation()).thenReturn(miraklProfessionalInformationMock);
		when(miraklShopMock.getAdditionalFieldValues())
				.thenReturn(List.of(miraklBankAccountTokenFieldValueMock, miraklHyperwalletProgramFieldValueMock));
		when(miraklBankAccountTokenFieldValueMock.getCode()).thenReturn(HYPERWALLET_BANK_ACCOUNT_TOKEN);
		when(miraklBankAccountTokenFieldValueMock.getValue()).thenReturn(TOKEN);
		when(miraklHyperwalletProgramFieldValueMock.getCode()).thenReturn(SellerModelConstants.HYPERWALLET_PROGRAM);
		when(miraklHyperwalletProgramFieldValueMock.getValue()).thenReturn(HYPERWALLET_PROGRAM);

		when(contactInformationMock.getFirstname()).thenReturn(FIRST_NAME);
		when(contactInformationMock.getLastname()).thenReturn(LAST_NAME);
		when(contactInformationMock.getStreet1()).thenReturn(STREET_1);
		when(contactInformationMock.getStreet2()).thenReturn(null);
		when(contactInformationMock.getCountry()).thenReturn(GB_COUNTRY);

		when(miraklUKBankAccountInformationMock.getBankSortCode()).thenReturn(SORT_CODE);
		when(miraklUKBankAccountInformationMock.getBankAccountNumber()).thenReturn(BANK_ACCOUNT_NUMBER);
		when(miraklUKBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final var result = testObj.execute(miraklShopMock);
		//@formatter:off
        assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", UK_COUNTRY_ISO)
                .hasFieldOrPropertyWithValue("transferMethodCurrency", GBP_CURRENCY)
                .hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
                .hasFieldOrPropertyWithValue("type", BankAccountType.UK)
                .hasFieldOrPropertyWithValue("bankAccountId", SORT_CODE)
                .hasFieldOrPropertyWithValue("bankAccountNumber", BANK_ACCOUNT_NUMBER)
                .hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
                .hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
                .hasFieldOrPropertyWithValue("lastName", LAST_NAME)
                .hasFieldOrPropertyWithValue("country", UK_COUNTRY_ISO)
                .hasFieldOrPropertyWithValue("addressLine1", STREET_1)
                .hasFieldOrPropertyWithValue("addressLine2", StringUtils.EMPTY)
                .hasFieldOrPropertyWithValue("city", CITY_NAME)
                .hasFieldOrPropertyWithValue("token", TOKEN)
                .hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
        //@formatter:on

	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentInformationIsUK() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklUKBankAccountInformationMock);

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentInformationIsNotIBAN() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklABABankAccountInformationMock);

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenNullPaymentInformationIsReceived() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(null);

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

}
