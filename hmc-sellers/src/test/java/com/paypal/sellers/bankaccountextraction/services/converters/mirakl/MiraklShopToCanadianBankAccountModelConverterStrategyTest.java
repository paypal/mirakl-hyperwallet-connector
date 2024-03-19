package com.paypal.sellers.bankaccountextraction.services.converters.mirakl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklCanadianBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklIbanBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.TransferType;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyInfo;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyResolver;
import com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.HYPERWALLET_BANK_ACCOUNT_STATE;
import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.HYPERWALLET_BANK_ACCOUNT_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklShopToCanadianBankAccountModelConverterStrategyTest {

	private static final String FIRST_NAME = "firstName";

	private static final String LAST_NAME = "lastName";

	private static final String STREET_1 = "street1";

	private static final String STREET_2 = "street2";

	private static final String CONTACT_COUNTRY = "FRA";

	private static final String FR_COUNTRY_ISO = "FR";

	private static final String BUSINESS_NAME = "business_name";

	private static final String CA_COUNTRY_ISO = "CA";

	private static final String USD_CURRENCY = "USD";

	private static final String TOKEN = "bankAccountToken";

	private static final String STATE = "NEW YORK";

	private static final String INSTITUTION_NUMBER = "INSTITUTION_NUMBER";

	private static final String TRANSIT_NUMBER = "TRANSIT_NUMBER";

	private static final String CITY_NAME = "city";

	private static final String BANK_ACCOUNT_NUMBER = "123456789";

	private final static String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@InjectMocks
	private MiraklShopToCanadianBankAccountModelConverterStrategy testObj;

	@Mock
	private HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolverMock;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklContactInformation contactInformationMock;

	@Mock
	private MiraklCanadianBankAccountInformation miraklCanadianBankAccountInformationMock;

	@Mock
	private MiraklDefaultBillingInformation miraklDefaultBillingInformationMock;

	@Mock
	private MiraklDefaultBillingInformation.CorporateInformation miraklCorporateInformationMock;

	@Mock
	private MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue miraklBankAccountTokenFieldValueMock,
			miraklBankAccountStateFieldValueMock;

	@Mock
	private MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue miraklHyperwalletProgramFieldValueMock;

	@Mock
	private MiraklIbanBankAccountInformation miraklIBANBankAccountInformationMock;

	@Test
	void execute_ShouldTransformFromMiraklShopToCanadianAccountModel() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklCanadianBankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.USD);
		when(miraklShopMock.getDefaultBillingInformation()).thenReturn(miraklDefaultBillingInformationMock);
		when(miraklDefaultBillingInformationMock.getCorporateInformation()).thenReturn(miraklCorporateInformationMock);
		when(miraklShopMock.getAdditionalFieldValues())
				.thenReturn(List.of(miraklBankAccountTokenFieldValueMock, miraklBankAccountStateFieldValueMock,
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

		when(miraklCanadianBankAccountInformationMock.getInstitutionNumber()).thenReturn(INSTITUTION_NUMBER);
		when(miraklCanadianBankAccountInformationMock.getTransitNumber()).thenReturn(TRANSIT_NUMBER);
		when(miraklCanadianBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);
		when(miraklCanadianBankAccountInformationMock.getBankAccountNumber()).thenReturn(BANK_ACCOUNT_NUMBER);

		when(miraklCorporateInformationMock.getCompanyRegistrationName()).thenReturn(BUSINESS_NAME);

		final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo = new HyperwalletBankAccountCurrencyInfo(
				CA_COUNTRY_ISO, USD_CURRENCY, TransferType.BANK_ACCOUNT);
		when(hyperwalletBankAccountCurrencyResolverMock.getCurrencyForCountry(BankAccountType.CANADIAN.name(),
				CA_COUNTRY_ISO, USD_CURRENCY)).thenReturn(hyperwalletBankAccountCurrencyInfo);

		final BankAccountModel result = testObj.execute(miraklShopMock);

		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", CA_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferMethodCurrency", USD_CURRENCY)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.CANADIAN)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", FR_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("addressLine1", STREET_1)
				.hasFieldOrPropertyWithValue("addressLine2", STREET_2)
				.hasFieldOrPropertyWithValue("stateProvince", STATE)
				.hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("branchId", TRANSIT_NUMBER)
				.hasFieldOrPropertyWithValue("bankId", INSTITUTION_NUMBER)
				.hasFieldOrPropertyWithValue("bankAccountNumber", BANK_ACCOUNT_NUMBER)
				.hasFieldOrPropertyWithValue("city", CITY_NAME)
				.hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
		//@formatter:on
	}

	@Test
	void execute_shouldEnsureThatOptionalFieldLineAddress2IsFilledWithAnEmptyString() {
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklCanadianBankAccountInformationMock);
		when(miraklShopMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.USD);
		when(miraklShopMock.getDefaultBillingInformation()).thenReturn(miraklDefaultBillingInformationMock);
		when(miraklDefaultBillingInformationMock.getCorporateInformation()).thenReturn(miraklCorporateInformationMock);
		when(miraklShopMock.getAdditionalFieldValues())
				.thenReturn(List.of(miraklBankAccountTokenFieldValueMock, miraklBankAccountStateFieldValueMock,
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

		when(miraklCanadianBankAccountInformationMock.getInstitutionNumber()).thenReturn(INSTITUTION_NUMBER);
		when(miraklCanadianBankAccountInformationMock.getTransitNumber()).thenReturn(TRANSIT_NUMBER);
		when(miraklCanadianBankAccountInformationMock.getBankCity()).thenReturn(CITY_NAME);
		when(miraklCanadianBankAccountInformationMock.getBankAccountNumber()).thenReturn(BANK_ACCOUNT_NUMBER);

		when(miraklCorporateInformationMock.getCompanyRegistrationName()).thenReturn(BUSINESS_NAME);

		final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo = new HyperwalletBankAccountCurrencyInfo(
				CA_COUNTRY_ISO, USD_CURRENCY, TransferType.BANK_ACCOUNT);
		when(hyperwalletBankAccountCurrencyResolverMock.getCurrencyForCountry(BankAccountType.CANADIAN.name(),
				CA_COUNTRY_ISO, USD_CURRENCY)).thenReturn(hyperwalletBankAccountCurrencyInfo);

		final BankAccountModel result = testObj.execute(miraklShopMock);
		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("transferMethodCountry", CA_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("transferMethodCurrency", USD_CURRENCY)
				.hasFieldOrPropertyWithValue("transferType", TransferType.BANK_ACCOUNT)
				.hasFieldOrPropertyWithValue("type", BankAccountType.CANADIAN)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("country", FR_COUNTRY_ISO)
				.hasFieldOrPropertyWithValue("addressLine1", STREET_1)
				.hasFieldOrPropertyWithValue("addressLine2", StringUtils.EMPTY)
				.hasFieldOrPropertyWithValue("stateProvince", STATE)
				.hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("branchId", TRANSIT_NUMBER)
				.hasFieldOrPropertyWithValue("bankId", INSTITUTION_NUMBER)
				.hasFieldOrPropertyWithValue("bankAccountNumber", BANK_ACCOUNT_NUMBER)
				.hasFieldOrPropertyWithValue("city", CITY_NAME)
				.hasFieldOrPropertyWithValue("hyperwalletProgram", HYPERWALLET_PROGRAM);
		//@formatter:on
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentInformationIsCanadian() {
		when(miraklShopMock.getPaymentInformation()).thenReturn(miraklCanadianBankAccountInformationMock);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentInformationIsNotCanadian() {
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
