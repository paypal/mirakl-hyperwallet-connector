package com.paypal.sellers.bankaccountextract.converter.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.bankaccountextract.converter.impl.sellermodel.AbstractSellerModelToHyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.IBANBankAccountModel;
import com.paypal.sellers.bankaccountextract.model.TransferType;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractSellerModelToHyperwalletBankAccountConverterTest {

	private static final String FIRST_NAME = "John";

	private static final String SECOND_NAME = "Doe";

	private static final String ADDRESS_LINE_ONE = "Elmo Street";

	private static final String ADDRESS_LINE_TWO = "Door 1";

	private static final String BUSINESS_NAME = "Super Business";

	private static final String CITY = "Wonder Town";

	private static final String COUNTRY = "USA";

	private static final String CURRENCY = "USD";

	private static final String USER_TOKEN = "124657895635121";

	private static final String BIC_CODE = "BIC";

	private static final String IBAN_ACCOUNT = "IBAN";

	private static final String BANK_ACCOUNT_TOKEN = "BANK_ACCOUNT_TOKEN";

	@InjectMocks
	private MySellerModelToHyperwalletBankAccount testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private IBANBankAccountModel IBANBankAccountModelMock;

	@Test
	void convert_shouldAddIndividualAttributes_whenIndividualModelIsReceived() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(IBANBankAccountModelMock);
		when(IBANBankAccountModelMock.getAddressLine1()).thenReturn(ADDRESS_LINE_ONE);
		when(IBANBankAccountModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_TWO);
		when(IBANBankAccountModelMock.getBankAccountNumber()).thenReturn(IBAN_ACCOUNT);
		when(IBANBankAccountModelMock.getTransferMethodCountry()).thenReturn(COUNTRY);
		when(IBANBankAccountModelMock.getTransferMethodCurrency()).thenReturn(CURRENCY);
		when(IBANBankAccountModelMock.getTransferType()).thenReturn(TransferType.BANK_ACCOUNT);
		when(IBANBankAccountModelMock.getCountry()).thenReturn(COUNTRY);
		when(IBANBankAccountModelMock.getCity()).thenReturn(CITY);
		when(sellerModelMock.getToken()).thenReturn(USER_TOKEN);
		when(sellerModelMock.getProfileType()).thenReturn(SellerProfileType.INDIVIDUAL);
		when(IBANBankAccountModelMock.getFirstName()).thenReturn(FIRST_NAME);
		when(IBANBankAccountModelMock.getLastName()).thenReturn(SECOND_NAME);
		when(IBANBankAccountModelMock.getToken()).thenReturn(BANK_ACCOUNT_TOKEN);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result.getAddressLine1()).isEqualTo(ADDRESS_LINE_ONE);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_TWO);
		assertThat(result.getBankAccountId()).isEqualTo(IBAN_ACCOUNT);
		assertThat(result.getTransferMethodCountry()).isEqualTo(COUNTRY);
		assertThat(result.getTransferMethodCurrency()).isEqualTo(CURRENCY);
		assertThat(result.getType()).isEqualTo(HyperwalletBankAccount.Type.BANK_ACCOUNT);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getProfileType()).isEqualTo(HyperwalletUser.ProfileType.INDIVIDUAL);
		assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(result.getLastName()).isEqualTo(SECOND_NAME);
		assertThat(result.getToken()).isEqualTo(BANK_ACCOUNT_TOKEN);
	}

	@Test
	void convert_shouldAddProfessionalAttributes_whenProfessionalModelIsReceived() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(IBANBankAccountModelMock);
		when(IBANBankAccountModelMock.getAddressLine1()).thenReturn(ADDRESS_LINE_ONE);
		when(IBANBankAccountModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_TWO);
		when(IBANBankAccountModelMock.getBankAccountNumber()).thenReturn(IBAN_ACCOUNT);
		when(IBANBankAccountModelMock.getTransferMethodCountry()).thenReturn(COUNTRY);
		when(IBANBankAccountModelMock.getTransferMethodCurrency()).thenReturn(CURRENCY);
		when(IBANBankAccountModelMock.getTransferType()).thenReturn(TransferType.BANK_ACCOUNT);
		when(IBANBankAccountModelMock.getCountry()).thenReturn(COUNTRY);
		when(IBANBankAccountModelMock.getCity()).thenReturn(CITY);
		when(sellerModelMock.getToken()).thenReturn(USER_TOKEN);
		when(sellerModelMock.getProfileType()).thenReturn(SellerProfileType.BUSINESS);
		when(sellerModelMock.getBusinessName()).thenReturn(BUSINESS_NAME);
		when(IBANBankAccountModelMock.getToken()).thenReturn(BANK_ACCOUNT_TOKEN);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result.getAddressLine1()).isEqualTo(ADDRESS_LINE_ONE);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_TWO);
		assertThat(result.getBankAccountId()).isEqualTo(IBAN_ACCOUNT);
		assertThat(result.getTransferMethodCountry()).isEqualTo(COUNTRY);
		assertThat(result.getTransferMethodCurrency()).isEqualTo(CURRENCY);
		assertThat(result.getType()).isEqualTo(HyperwalletBankAccount.Type.BANK_ACCOUNT);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getProfileType()).isEqualTo(HyperwalletUser.ProfileType.BUSINESS);
		assertThat(result.getBusinessName()).isEqualTo(BUSINESS_NAME);
		assertThat(result.getToken()).isEqualTo(BANK_ACCOUNT_TOKEN);
	}

	@Test
	void convert_shouldReturnNull_whenSellerModelHasNoBankAccountAssociated() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(null);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result).isNull();
	}

	@Test
	void execute_shouldReturnNull_whenNullPaymentInformationIsReceived() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(null);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result).isNull();
	}

	private static class MySellerModelToHyperwalletBankAccount extends AbstractSellerModelToHyperwalletBankAccount {

		@Override
		public boolean isApplicable(final SellerModel source) {
			return true;
		}

	}

}
