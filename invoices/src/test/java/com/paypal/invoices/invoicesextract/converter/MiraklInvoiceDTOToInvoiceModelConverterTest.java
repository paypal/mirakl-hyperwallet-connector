package com.paypal.invoices.invoicesextract.converter;

import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.dto.MiraklInvoiceMockDTO;
import com.paypal.invoices.dto.converter.MiraklInvoiceDTOToInvoiceModelConverter;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklInvoiceDTOToInvoiceModelConverterTest {

	private static final String SHOP_ID = "1245";

	private static final String SHOP_NAME = "Star Corporate SL";

	private static final String SHOP_CORPORATE_NAME = "Start Corporate";

	private static final String SHOP_ADDRESS_LAST_NAME = "Building 35";

	private static final String SHOP_ADDRESS_FIRST_NAME = "New Jersey Street 12";

	private static final String SHOP_ADDRESS_STREET_1 = "Master Street";

	private static final String SHOP_ADDRESS_STREET_2 = "Building 12";

	private static final String SHOP_ADDRESS_COMPLEMENTARY = "Complementary";

	private static final String SHOP_ADDRESS_ZIP_CODE = "46001";

	private static final String SHOP_ADDRESS_CITY = "Valencia";

	private static final String SHOP_ADDRESS_COUNTRY = "ES";

	private static final String SHOP_ADDRESS_STATE = "Valencian Community";

	private static final String SHOP_EMAIL = "star@corporate.com";

	private static final String SHOP_VAT_NUMBER = "48421885A";

	private static final String INVOICE_NUMBER = "INV-1245";

	private static final String PAYMENT_INFO_OWNER = "John Doe";

	private static final String PAYMENT_INFO_BANK_NAME = "Deutche Bank";

	private static final String PAYMENT_INFO_BANK_STREET = "Groening Street 12";

	private static final String PAYMENT_INFO_BANK_ZIP = "95421";

	private static final String PAYMENT_INFO_BANK_CITY = "Berlin";

	private static final String PAYMENT_INFO_BIC = "BICXXXX";

	private static final String PAYMENT_INFO_IBAN = "GE123456789123";

	private static final String SHOP_OPERATOR_INTERNAL_ID = "INT-123456";

	private static final String SHOP_IDENTIFICATION_NUMBER = "ID-123456";

	private static final String CURRENCY_ISO_CODE = "EUR";

	private static final String PAYMENT_INFO_TYPE = "BANK_ACCOUNT";

	@InjectMocks
	private MiraklInvoiceDTOToInvoiceModelConverter testObj;

	@SuppressWarnings("java:S5961")
	@Test
	void convert_shouldMapEveryAttributeOfMiraklInvoiceMockDTOIntoInvoiceModel() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2000, 11, 5, 12, 30, 22));
		final LocalDateTime now = TimeMachine.now();
		final Date fixedDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final MiraklInvoiceMockDTO miraklInvoiceMockDTO = createMiraklInvoiceMockDTO();

		final InvoiceModel result = testObj.convert(miraklInvoiceMockDTO);

		assertThat(result.getShopId()).isEqualTo(SHOP_ID);
		assertThat(result.getShopName()).isEqualTo(SHOP_NAME);
		assertThat(result.getShopCorporateName()).isEqualTo(SHOP_CORPORATE_NAME);
		assertThat(result.getShopAddressLastName()).isEqualTo(SHOP_ADDRESS_LAST_NAME);
		assertThat(result.getShopAddressFirstName()).isEqualTo(SHOP_ADDRESS_FIRST_NAME);
		assertThat(result.getShopAddressStreet1()).isEqualTo(SHOP_ADDRESS_STREET_1);
		assertThat(result.getShopAddressStreet2()).isEqualTo(SHOP_ADDRESS_STREET_2);
		assertThat(result.getShopAddressComplementary()).isEqualTo(SHOP_ADDRESS_COMPLEMENTARY);
		assertThat(result.getShopAddressZipCode()).isEqualTo(SHOP_ADDRESS_ZIP_CODE);
		assertThat(result.getShopAddressCity()).isEqualTo(SHOP_ADDRESS_CITY);
		assertThat(result.getShopAddressCountry()).isEqualTo(SHOP_ADDRESS_COUNTRY);
		assertThat(result.getShopAddressState()).isEqualTo(SHOP_ADDRESS_STATE);
		assertThat(result.getShopEmail()).isEqualTo(SHOP_EMAIL);
		assertThat(result.isShopIsProfessional()).isFalse();
		assertThat(result.getShopVatNumber()).isEqualTo(SHOP_VAT_NUMBER);
		assertThat(result.getDateCreated()).isEqualTo(fixedDate);
		assertThat(result.getStartTime()).isEqualTo(fixedDate);
		assertThat(result.getEndTime()).isEqualTo(fixedDate);
		assertThat(result.getInvoiceNumber()).isEqualTo(INVOICE_NUMBER);
		assertThat(result.getPaymentInfoOwner()).isEqualTo(PAYMENT_INFO_OWNER);
		assertThat(result.getPaymentInfoBankName()).isEqualTo(PAYMENT_INFO_BANK_NAME);
		assertThat(result.getPaymentInfoBankStreet()).isEqualTo(PAYMENT_INFO_BANK_STREET);
		assertThat(result.getPaymentInfoBankZip()).isEqualTo(PAYMENT_INFO_BANK_ZIP);
		assertThat(result.getPaymentInfoBankCity()).isEqualTo(PAYMENT_INFO_BANK_CITY);
		assertThat(result.getPaymentInfoBic()).isEqualTo(PAYMENT_INFO_BIC);
		assertThat(result.getPaymentInfoIban()).isEqualTo(PAYMENT_INFO_IBAN);
		assertThat(result.getOrderAmount()).isEqualTo(100.D);
		assertThat(result.getOrderShippingAmount()).isEqualTo(10.0D);
		assertThat(result.getOrderCommissionAmount()).isEqualTo(1.0D);
		assertThat(result.getOrderCommissionAmountVat()).isEqualTo(2.0D);
		assertThat(result.getRefundAmount()).isEqualTo(3.0D);
		assertThat(result.getRefundShippingAmount()).isEqualTo(4.0D);
		assertThat(result.getRefundCommissionAmount()).isEqualTo(5.0D);
		assertThat(result.getRefundCommissionAmountVat()).isEqualTo(6.0D);
		assertThat(result.getSubscriptionAmount()).isEqualTo(7.0D);
		assertThat(result.getSubscriptionAmountVat()).isEqualTo(8.0D);
		assertThat(result.getTotalChargedAmount()).isEqualTo(9.0D);
		assertThat(result.getTotalChargedAmountVat()).isEqualTo(110.0D);
		assertThat(result.getTransferAmount()).isEqualTo(12.0D);
		assertThat(result.getShopOperatorInternalId()).isEqualTo(SHOP_OPERATOR_INTERNAL_ID);
		assertThat(result.getShopIdentificationNumber()).isEqualTo(SHOP_IDENTIFICATION_NUMBER);
		assertThat(result.getTotalManualInvoiceAmount()).isEqualTo(20.0D);
		assertThat(result.getTotalManualInvoiceAmountVat()).isEqualTo(21.0D);
		assertThat(result.getTotalManualCreditAmount()).isEqualTo(22.0D);
		assertThat(result.getTotalManualCreditAmountVat()).isEqualTo(23.0D);
		assertThat(result.getCurrencyIsoCode()).isEqualTo(CURRENCY_ISO_CODE);
		assertThat(result.getPaymentInfoType()).isEqualTo(PAYMENT_INFO_TYPE);
	}

	@Test
	void convert_shouldReturnNullWhenNullObjectIsReceived() {
		final InvoiceModel result = testObj.convert(null);
		assertThat(result).isNull();
	}

	private MiraklInvoiceMockDTO createMiraklInvoiceMockDTO() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2000, 11, 5, 12, 30, 22));
		final LocalDateTime now = TimeMachine.now();
		final Date fixedDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final MiraklInvoiceMockDTO miraklInvoiceMockDTO = new MiraklInvoiceMockDTO();
		miraklInvoiceMockDTO.setShopId(SHOP_ID);
		miraklInvoiceMockDTO.setShopName(SHOP_NAME);
		miraklInvoiceMockDTO.setShopCorporateName(SHOP_CORPORATE_NAME);
		miraklInvoiceMockDTO.setShopAddressLastName(SHOP_ADDRESS_LAST_NAME);
		miraklInvoiceMockDTO.setShopAddressFirstName(SHOP_ADDRESS_FIRST_NAME);
		miraklInvoiceMockDTO.setShopAddressStreet1(SHOP_ADDRESS_STREET_1);
		miraklInvoiceMockDTO.setShopAddressStreet2(SHOP_ADDRESS_STREET_2);
		miraklInvoiceMockDTO.setShopAddressComplementary(SHOP_ADDRESS_COMPLEMENTARY);
		miraklInvoiceMockDTO.setShopAddressZipCode(SHOP_ADDRESS_ZIP_CODE);
		miraklInvoiceMockDTO.setShopAddressCity(SHOP_ADDRESS_CITY);
		miraklInvoiceMockDTO.setShopAddressCountry(SHOP_ADDRESS_COUNTRY);
		miraklInvoiceMockDTO.setShopAddressState(SHOP_ADDRESS_STATE);
		miraklInvoiceMockDTO.setShopEmail(SHOP_EMAIL);
		miraklInvoiceMockDTO.setShopIsProfessional(false);
		miraklInvoiceMockDTO.setShopVatNumber(SHOP_VAT_NUMBER);
		miraklInvoiceMockDTO.setDateCreated(fixedDate);
		miraklInvoiceMockDTO.setStartTime(fixedDate);
		miraklInvoiceMockDTO.setEndTime(fixedDate);
		miraklInvoiceMockDTO.setInvoiceNumber(INVOICE_NUMBER);
		miraklInvoiceMockDTO.setPaymentInfoOwner(PAYMENT_INFO_OWNER);
		miraklInvoiceMockDTO.setPaymentInfoBankName(PAYMENT_INFO_BANK_NAME);
		miraklInvoiceMockDTO.setPaymentInfoBankStreet(PAYMENT_INFO_BANK_STREET);
		miraklInvoiceMockDTO.setPaymentInfoBankZip(PAYMENT_INFO_BANK_ZIP);
		miraklInvoiceMockDTO.setPaymentInfoBankCity(PAYMENT_INFO_BANK_CITY);
		miraklInvoiceMockDTO.setPaymentInfoBic(PAYMENT_INFO_BIC);
		miraklInvoiceMockDTO.setPaymentInfoIban(PAYMENT_INFO_IBAN);
		miraklInvoiceMockDTO.setOrderAmount(100.0D);
		miraklInvoiceMockDTO.setOrderShippingAmount(10.0D);
		miraklInvoiceMockDTO.setOrderCommissionAmount(1.0D);
		miraklInvoiceMockDTO.setOrderCommissionAmountVat(2.0D);
		miraklInvoiceMockDTO.setRefundAmount(3.0D);
		miraklInvoiceMockDTO.setRefundShippingAmount(4.0D);
		miraklInvoiceMockDTO.setRefundCommssionAmount(5.0D);
		miraklInvoiceMockDTO.setRefundCommissionAmountVat(6.0D);
		miraklInvoiceMockDTO.setSubscriptionAmount(7.0D);
		miraklInvoiceMockDTO.setSubscriptionAmountVat(8.0D);
		miraklInvoiceMockDTO.setTotalChargedAmount(9.0D);
		miraklInvoiceMockDTO.setTotalChargedAmountVat(110.0D);
		miraklInvoiceMockDTO.setTransferAmount(12.0D);
		miraklInvoiceMockDTO.setShopOperatorInternalId(SHOP_OPERATOR_INTERNAL_ID);
		miraklInvoiceMockDTO.setShopIdentificationNumber(SHOP_IDENTIFICATION_NUMBER);
		miraklInvoiceMockDTO.setTotalManualInvoiceAmount(20.0D);
		miraklInvoiceMockDTO.setTotalManualInvoiceAmountVat(21.0D);
		miraklInvoiceMockDTO.setTotalManualCreditAmount(22.0D);
		miraklInvoiceMockDTO.setTotalManualCreditAmountVat(23.0D);
		miraklInvoiceMockDTO.setCurrencyIsoCode(CURRENCY_ISO_CODE);
		miraklInvoiceMockDTO.setPaymentInfoType(PAYMENT_INFO_TYPE);

		return miraklInvoiceMockDTO;
	}

}
