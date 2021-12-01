package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklAbaBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklIbanBankAccountInformation;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;
import com.paypal.sellers.bankaccountextract.model.IBANBankAccountModel;
import com.paypal.sellers.bankaccountextract.model.TransferType;
import com.paypal.sellers.sellersextract.model.SellerModelConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.sellers.sellersextract.model.SellerModelConstants.HYPERWALLET_BANK_ACCOUNT_STATE;
import static com.paypal.sellers.sellersextract.model.SellerModelConstants.HYPERWALLET_BANK_ACCOUNT_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklShopToIBANBankAccountModelConverterStrategyTest {

	private static final String FIRST_NAME = "firstName";

	private static final String LAST_NAME = "lastName";

	private static final String STREET_1 = "street1";

	private static final String STREET_2 = "street2";

	private static final String CITY_NAME = "city";

	private static final String SPAIN_COUNTRY = "ESP";

	private static final String BIC_CODE = "BIC";

	private static final String IBAN_ACCOUNT = "IBAN";

	private static final String BUSINESS_NAME = "business_name";

	private static final String ES_COUNTRY_ISO = "ES";

	private static final String EUR_CURRENCY = "EUR";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String TOKEN = "bankAccountToken";

	private static final String STATE = "bankAccountState";

	@InjectMocks
	private MiraklShopToIBANBankAccountModelConverterStrategy testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklContactInformation contactInformationMock;

	@Mock
	private MiraklIbanBankAccountInformation miraklIbanBankAccountInformationMock;

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
	void convert_ShouldTransformFromMiraklShopToIbanBankAccountModel() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklIbanBankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
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
		when(miraklIbanBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);
		when(contactInformationMock.getCountry()).thenReturn(SPAIN_COUNTRY);

		when(miraklIbanBankAccountInformationMock.getBic()).thenReturn(BIC_CODE);
		when(miraklIbanBankAccountInformationMock.getIban()).thenReturn(IBAN_ACCOUNT);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final IBANBankAccountModel result = testObj.execute(miraklShopMock);
		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", ES_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferMethodCurrency", EUR_CURRENCY)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.IBAN)
				.hasFieldOrPropertyWithValue("bankBic", BIC_CODE)
				.hasFieldOrPropertyWithValue("bankAccountNumber", IBAN_ACCOUNT)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", ES_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("addressLine1", STREET_1)
				.hasFieldOrPropertyWithValue("addressLine2", STREET_2)
				.hasFieldOrPropertyWithValue("city", CITY_NAME)
				.hasFieldOrPropertyWithValue("stateProvince", STATE)
				.hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
		//@formatter:on
	}

	@Test
	void convert_ShouldTransformFromMiraklShopToUKIbanBankAccountModel() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklIbanBankAccountInformationMock);
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
		when(miraklIbanBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);
		when(contactInformationMock.getCountry()).thenReturn(SPAIN_COUNTRY);

		when(miraklIbanBankAccountInformationMock.getBic()).thenReturn(BIC_CODE);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final IBANBankAccountModel result = testObj.execute(miraklShopMock);
		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", ES_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.IBAN)
				.hasFieldOrPropertyWithValue("bankBic", BIC_CODE)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", ES_COUNTRY_ISO)
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
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklIbanBankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
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
		when(contactInformationMock.getCountry()).thenReturn(SPAIN_COUNTRY);

		when(miraklIbanBankAccountInformationMock.getBic()).thenReturn(BIC_CODE);
		when(miraklIbanBankAccountInformationMock.getIban()).thenReturn(IBAN_ACCOUNT);
		when(miraklIbanBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final IBANBankAccountModel result = testObj.execute(miraklShopMock);
		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", ES_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferMethodCurrency", EUR_CURRENCY)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.IBAN)
				.hasFieldOrPropertyWithValue("bankBic", BIC_CODE)
				.hasFieldOrPropertyWithValue("bankAccountNumber", IBAN_ACCOUNT)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", ES_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("addressLine1", STREET_1)
				.hasFieldOrPropertyWithValue("addressLine2", StringUtils.EMPTY)
				.hasFieldOrPropertyWithValue("city", CITY_NAME)
				.hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
		//@formatter:on
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentInformationIsIBAN() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklIbanBankAccountInformationMock);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentInformationIsNotIBAN() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklABABankAccountInformationMock);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenNullPaymentInformationIsReceived() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(null);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

}
