package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklAbaBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklIbanBankAccountInformation;
import com.paypal.sellers.bankaccountextract.model.ABABankAccountModel;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;
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
class MiraklShopToABABankAccountModelConverterStrategyTest {

	private static final String FIRST_NAME = "firstName";

	private static final String LAST_NAME = "lastName";

	private static final String STREET_1 = "street1";

	private static final String STREET_2 = "street2";

	private static final String CITY_NAME = "city";

	private static final String ABA_ACCOUNT = "ABA";

	private static final String BUSINESS_NAME = "business_name";

	private static final String CONTACT_COUNTRY = "FRA";

	private static final String FR_COUNTRY_ISO = "FR";

	private static final String USA_COUNTRY_ISO = "US";

	private static final String USD_CURRENCY = "USD";

	private static final String TOKEN = "bankAccountToken";

	private static final String STATE = "NEW YORK";

	private static final String BANK_ZIP = "NY 10036 US";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@InjectMocks
	private MiraklShopToABABankAccountModelConverterStrategy testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklContactInformation contactInformationMock;

	@Mock
	private MiraklAbaBankAccountInformation miraklABABankAccountInformationMock;

	@Mock
	private MiraklProfessionalInformation miraklProfessionalInformationMock;

	@Mock
	private MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue miraklBankAccountTokenFieldValueMock,
			miraklBankAccountStateFieldValueMock;

	@Mock
	private MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue miraklHyperwalletProgramFieldValueMock;

	@Mock
	private MiraklIbanBankAccountInformation miraklIBANBankAccountInformationMock;

	@Test
	void execute_ShouldTransformFromMiraklShopToABAAccountModel() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklABABankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.USD);
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
		when(contactInformationMock.getCountry()).thenReturn(CONTACT_COUNTRY);

		when(miraklABABankAccountInformationMock.getBankZip()).thenReturn(BANK_ZIP);
		when(miraklABABankAccountInformationMock.getBankAccountNumber()).thenReturn(ABA_ACCOUNT);
		when(miraklABABankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final ABABankAccountModel result = testObj.execute(miraklShopMock);

		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", USA_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferMethodCurrency", USD_CURRENCY)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.ABA)
				.hasFieldOrPropertyWithValue("bankAccountNumber", ABA_ACCOUNT)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", FR_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("addressLine1", STREET_1)
				.hasFieldOrPropertyWithValue("addressLine2", STREET_2)
				.hasFieldOrPropertyWithValue("city", CITY_NAME)
				.hasFieldOrPropertyWithValue("stateProvince", STATE)
				.hasFieldOrPropertyWithValue("postalCode", BANK_ZIP)
				.hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
		//@formatter:on
	}

	@Test
	void execute_shouldEnsureThatOptionalFieldLineAddress2IsFilledWithAnEmptyString() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklABABankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.USD);
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
		when(contactInformationMock.getStreet2()).thenReturn(null);
		when(contactInformationMock.getCountry()).thenReturn(CONTACT_COUNTRY);

		when(miraklABABankAccountInformationMock.getBankZip()).thenReturn(BANK_ZIP);
		when(miraklABABankAccountInformationMock.getBankAccountNumber()).thenReturn(ABA_ACCOUNT);
		when(miraklABABankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);

		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(BUSINESS_NAME);

		final ABABankAccountModel result = testObj.execute(miraklShopMock);
		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", USA_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferMethodCurrency", USD_CURRENCY)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.ABA)
				.hasFieldOrPropertyWithValue("bankAccountNumber", ABA_ACCOUNT)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", FR_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("addressLine1", STREET_1)
				.hasFieldOrPropertyWithValue("addressLine2", StringUtils.EMPTY)
				.hasFieldOrPropertyWithValue("city", CITY_NAME)
				.hasFieldOrPropertyWithValue("stateProvince", STATE)
				.hasFieldOrPropertyWithValue("postalCode", BANK_ZIP)
				.hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
		//@formatter:on
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentInformationIsABA() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklABABankAccountInformationMock);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentInformationIsNotABA() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklIBANBankAccountInformationMock);

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
