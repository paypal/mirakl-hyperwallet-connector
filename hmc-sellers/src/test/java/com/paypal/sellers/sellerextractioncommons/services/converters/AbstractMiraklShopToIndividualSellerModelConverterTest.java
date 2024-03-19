package com.paypal.sellers.sellerextractioncommons.services.converters;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.IBANBankAccountModel;
import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel.SellerModelBuilder;
import com.paypal.sellers.utils.LanguageConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractMiraklShopToIndividualSellerModelConverterTest {

	@InjectMocks
	private MyAbstractMiraklShopToSellerModelConverter testObj;

	@Mock
	private SellersMiraklApiConfig sellersMiraklApiConfigMock;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklContactInformation contactInformationMock;

	@Mock
	private StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor;

	@Mock
	private IBANBankAccountModel IBANBankAccountModelMock;

	@Mock
	private MiraklDefaultBillingInformation miraklDefaultBillingInformationMock;

	@Mock
	private LanguageConverter languageConversionMock;

	@Test
	void convert_ShouldTransformFromMiraklShopToProfessionalSeller() {
		when(sellersMiraklApiConfigMock.getTimeZone()).thenReturn("UTC");
		when(miraklShopMock.getId()).thenReturn("shopId");
		when(miraklShopMock.getName()).thenReturn("shopName");
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getDefaultBillingInformation()).thenReturn(miraklDefaultBillingInformationMock);
		when(miraklDefaultBillingInformationMock.getDefaultLanguage()).thenReturn(Locale.US);
		when(contactInformationMock.getFirstname()).thenReturn("firstName");
		when(contactInformationMock.getLastname()).thenReturn("lastName");
		when(contactInformationMock.getPhone()).thenReturn("phone");
		when(contactInformationMock.getPhoneSecondary()).thenReturn("secondaryPhone");
		when(contactInformationMock.getEmail()).thenReturn("email@example.com");
		when(contactInformationMock.getStreet1()).thenReturn("street1");
		when(contactInformationMock.getStreet2()).thenReturn("street2");
		when(contactInformationMock.getCity()).thenReturn("city");
		when(contactInformationMock.getZipCode()).thenReturn("zipcode");
		when(contactInformationMock.getState()).thenReturn("state");
		when(contactInformationMock.getCountry()).thenReturn("USA");
		// Not testing the builder part where it is converting and setting values from
		// mirakl custom fields
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(Collections.emptyList());

		when(miraklShopBankAccountModelStrategyExecutor.execute(miraklShopMock)).thenReturn(IBANBankAccountModelMock);
		when(languageConversionMock.convert(Locale.US)).thenReturn("en");

		final SellerModelBuilder result = testObj.getCommonFieldsBuilder(miraklShopMock);

		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("clientUserId", "shopId")
				.hasFieldOrPropertyWithValue("businessName", "shopName")
				.hasFieldOrPropertyWithValue("firstName", "firstName")
				.hasFieldOrPropertyWithValue("lastName", "lastName")
				.hasFieldOrPropertyWithValue("phoneNumber", "phone")
				.hasFieldOrPropertyWithValue("mobilePhone", "secondaryPhone")
				.hasFieldOrPropertyWithValue("email", "email@example.com")
				.hasFieldOrPropertyWithValue("addressLine1", "street1")
				.hasFieldOrPropertyWithValue("addressLine2", "street2")
				.hasFieldOrPropertyWithValue("city", "city")
				.hasFieldOrPropertyWithValue("postalCode", "zipcode")
				.hasFieldOrPropertyWithValue("stateProvince", "state")
				.hasFieldOrPropertyWithValue("country", "US")
				.hasFieldOrPropertyWithValue("bankAccountDetails", IBANBankAccountModelMock)
				.hasFieldOrPropertyWithValue("language", "en");
		//@formatter:on
	}

	@Test
	void convert_ShouldTransformFromMiraklShopToProfessionalSellerWhenBankAccountDetailsAreNullOrEmpty() {
		when(sellersMiraklApiConfigMock.getTimeZone()).thenReturn("UTC");
		when(miraklShopMock.getId()).thenReturn("shopId");
		when(miraklShopMock.getName()).thenReturn("shopName");
		when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
		when(miraklShopMock.getDefaultBillingInformation()).thenReturn(miraklDefaultBillingInformationMock);
		when(miraklDefaultBillingInformationMock.getDefaultLanguage()).thenReturn(Locale.US);
		when(contactInformationMock.getFirstname()).thenReturn("firstName");
		when(contactInformationMock.getLastname()).thenReturn("lastName");
		when(contactInformationMock.getPhone()).thenReturn("phone");
		when(contactInformationMock.getPhoneSecondary()).thenReturn("secondaryPhone");
		when(contactInformationMock.getEmail()).thenReturn("email@example.com");
		when(contactInformationMock.getStreet1()).thenReturn("street1");
		when(contactInformationMock.getStreet2()).thenReturn("street2");
		when(contactInformationMock.getCity()).thenReturn("city");
		when(contactInformationMock.getZipCode()).thenReturn("zipcode");
		when(contactInformationMock.getState()).thenReturn("state");
		when(contactInformationMock.getCountry()).thenReturn("USA");
		// Not testing the builder part where it is converting and setting values from
		// mirakl custom fields
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(Collections.emptyList());
		when(languageConversionMock.convert(Locale.US)).thenReturn("en");

		final SellerModelBuilder result = testObj.getCommonFieldsBuilder(miraklShopMock);
		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("clientUserId", "shopId")
				.hasFieldOrPropertyWithValue("businessName", "shopName")
				.hasFieldOrPropertyWithValue("firstName", "firstName")
				.hasFieldOrPropertyWithValue("lastName", "lastName")
				.hasFieldOrPropertyWithValue("phoneNumber", "phone")
				.hasFieldOrPropertyWithValue("mobilePhone", "secondaryPhone")
				.hasFieldOrPropertyWithValue("email", "email@example.com")
				.hasFieldOrPropertyWithValue("addressLine1", "street1")
				.hasFieldOrPropertyWithValue("addressLine2", "street2")
				.hasFieldOrPropertyWithValue("city", "city")
				.hasFieldOrPropertyWithValue("postalCode", "zipcode")
				.hasFieldOrPropertyWithValue("stateProvince", "state")
				.hasFieldOrPropertyWithValue("country", "US")
				.hasFieldOrPropertyWithValue("bankAccountDetails", null)
				.hasFieldOrPropertyWithValue("language", "en");
		//@formatter:on
	}

	private static class MyAbstractMiraklShopToSellerModelConverter extends AbstractMiraklShopToSellerModelConverter {

		protected MyAbstractMiraklShopToSellerModelConverter(
				final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
				final SellersMiraklApiConfig sellersMiraklApiConfig, final LanguageConverter languageConversion) {
			super(miraklShopBankAccountModelStrategyExecutor, sellersMiraklApiConfig, languageConversion);
		}

		@Override
		public SellerModel execute(final MiraklShop source) {
			return null;
		}

		@Override
		public boolean isApplicable(final MiraklShop source) {
			return false;
		}

	}

}
