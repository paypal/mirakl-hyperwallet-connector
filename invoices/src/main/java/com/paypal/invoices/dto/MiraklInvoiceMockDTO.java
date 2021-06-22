package com.paypal.invoices.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MiraklInvoiceMockDTO {

	@JsonProperty("hyperwallet-program")
	private String hyperwalletProgram;

	@JsonProperty("shop-id")
	private String shopId;

	@JsonProperty("shop-name")
	private String shopName;

	@JsonProperty("shop-corporate-name")
	private String shopCorporateName;

	@JsonProperty("shop-address-lastname")
	private String shopAddressLastName;

	@JsonProperty("shop-address-firstname")
	private String shopAddressFirstName;

	@JsonProperty("shop-address-street1")
	private String shopAddressStreet1;

	@JsonProperty("shop-address-street2")
	private String shopAddressStreet2;

	@JsonProperty("shop-address-complementary")
	private String shopAddressComplementary;

	@JsonProperty("shop-address-zip-code")
	private String shopAddressZipCode;

	@JsonProperty("shop-address-city")
	private String shopAddressCity;

	@JsonProperty("shop-address-country")
	private String shopAddressCountry;

	@JsonProperty("shop-address-state")
	private String shopAddressState;

	@JsonProperty("shop-email")
	private String shopEmail;

	@JsonProperty("shop-is-professional")
	private boolean shopIsProfessional;

	@JsonProperty("shop-vat-number")
	private String shopVatNumber;

	@JsonProperty("date-created")
	private Date dateCreated;

	@JsonProperty("start-time")
	private Date startTime;

	@JsonProperty("end-time")
	private Date endTime;

	@JsonProperty("invoice-number")
	private String invoiceNumber;

	@JsonProperty("payment-info-owner")
	private String paymentInfoOwner;

	@JsonProperty("payment-info-bank-name")
	private String paymentInfoBankName;

	@JsonProperty("payment-info-bank-street")
	private String paymentInfoBankStreet;

	@JsonProperty("payment-info-bank-zip")
	private String paymentInfoBankZip;

	@JsonProperty("payment-info-bank-city")
	private String paymentInfoBankCity;

	@JsonProperty("payment-info-bic")
	private String paymentInfoBic;

	@JsonProperty("payment-info-iban")
	private String paymentInfoIban;

	@JsonProperty("order-amount")
	private Double orderAmount;

	@JsonProperty("order-shipping-amount")
	private Double orderShippingAmount;

	@JsonProperty("order-commission-amount")
	private Double orderCommissionAmount;

	@JsonProperty("order-commission-amount-vat")
	private Double orderCommisionAmountVat;

	@JsonProperty("refund-amount")
	private Double refundAmount;

	@JsonProperty("refund-shipping-amount")
	private Double refundShippingAmount;

	@JsonProperty("refund-commission-amount")
	private Double refundCommssionAmount;

	@JsonProperty("refund-commission-amount-vat")
	private Double refundCommisionAmountVat;

	@JsonProperty("subscription-amount")
	private Double subscriptionAmount;

	@JsonProperty("subscription-amount-vat")
	private Double subscriptionAmountVat;

	@JsonProperty("total-charged-amount")
	private Double totalChargedAmount;

	@JsonProperty("total-charged-amount-vat")
	private Double totalChargedAmountVat;

	@JsonProperty("transfer-amount")
	private Double transterAmount;

	@JsonProperty("shop-operator-internal-id")
	private String shopOperatorInternalId;

	@JsonProperty("shop-identification-number")
	private String shopIdentificationNumber;

	@JsonProperty("total-manual-invoice-amount")
	private Double totalManualInvoiceAmount;

	@JsonProperty("total-manual-invoice-amount-vat")
	private Double totalManualInvoiceAmountVat;

	@JsonProperty("total-manual-credit-amount")
	private Double totalManualCreditAmount;

	@JsonProperty("total-manual-credit-amount-vat")
	private Double totalManualCreditAmountVat;

	@JsonProperty("currency-iso-code")
	private String currencyIsoCode;

	@JsonProperty("payment-info-type")
	private String paymentInfoType;

	private String token;

	private String destinationToken;

}
