package com.paypal.invoices.invoicesextract.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.paypal.invoices.invoicesextract.model.InvoiceModelConstants.HYPERWALLET_BANK_ACCOUNT_TOKEN;
import static com.paypal.invoices.invoicesextract.model.InvoiceModelConstants.HYPERWALLET_PROGRAM;

@Getter
public class AccountingDocumentModel {

	protected final String shopId;

	protected final Date startTime;

	protected final Date endTime;

	protected final String shopName;

	protected final String shopCorporateName;

	protected final String shopAddressLastName;

	protected final String shopAddressFirstName;

	protected final String shopAddressStreet1;

	protected final String shopAddressStreet2;

	protected final String shopAddressComplementary;

	protected final String shopAddressZipCode;

	protected final String shopAddressCity;

	protected final String shopAddressCountry;

	protected final String shopAddressState;

	protected final String shopEmail;

	protected final boolean shopIsProfessional;

	protected final String shopVatNumber;

	protected final Date dateCreated;

	protected final String invoiceNumber;

	protected final String paymentInfoOwner;

	protected final String paymentInfoBankName;

	protected final String paymentInfoBankStreet;

	protected final String paymentInfoBankZip;

	protected final String paymentInfoBankCity;

	protected final String paymentInfoBic;

	protected final String paymentInfoIban;

	protected final Double orderAmount;

	protected final Double orderShippingAmount;

	protected final Double orderCommissionAmount;

	protected final Double refundAmount;

	protected final Double refundShippingAmount;

	protected final Double refundCommissionAmount;

	protected final Double refundCommissionAmountVat;

	protected final Double subscriptionAmount;

	protected final Double totalChargedAmount;

	protected final Double totalChargedAmountVat;

	protected final String shopOperatorInternalId;

	protected final String shopIdentificationNumber;

	protected final Double totalManualInvoiceAmount;

	protected final Double totalManualInvoiceAmountVat;

	protected final Double totalManualCreditAmount;

	protected final Double totalManualCreditAmountVat;

	protected final String currencyIsoCode;

	protected final String paymentInfoType;

	protected final String destinationToken;

	protected final String hyperwalletProgram;

	protected AccountingDocumentModel(final Builder<?> builder) {
		shopId = builder.shopId;
		startTime = builder.startTime;
		endTime = builder.endTime;
		shopName = builder.shopName;
		shopCorporateName = builder.shopCorporateName;
		shopAddressLastName = builder.shopAddressLastName;
		shopAddressFirstName = builder.shopAddressFirstName;
		shopAddressStreet1 = builder.shopAddressStreet1;
		shopAddressStreet2 = builder.shopAddressStreet2;
		shopAddressComplementary = builder.shopAddressComplementary;
		shopAddressZipCode = builder.shopAddressZipCode;
		shopAddressCity = builder.shopAddressCity;
		shopAddressCountry = builder.shopAddressCountry;
		shopAddressState = builder.shopAddressState;
		shopEmail = builder.shopEmail;
		shopIsProfessional = builder.shopIsProfessional;
		shopVatNumber = builder.shopVatNumber;
		dateCreated = builder.dateCreated;
		invoiceNumber = builder.invoiceNumber;
		paymentInfoOwner = builder.paymentInfoOwner;
		paymentInfoBankName = builder.paymentInfoBankName;
		paymentInfoBankStreet = builder.paymentInfoBankStreet;
		paymentInfoBankZip = builder.paymentInfoBankZip;
		paymentInfoBankCity = builder.paymentInfoBankCity;
		paymentInfoBic = builder.paymentInfoBic;
		paymentInfoIban = builder.paymentInfoIban;
		orderAmount = builder.orderAmount;
		orderShippingAmount = builder.orderShippingAmount;
		orderCommissionAmount = builder.orderCommissionAmount;
		refundAmount = builder.refundAmount;
		refundShippingAmount = builder.refundShippingAmount;
		refundCommissionAmount = builder.refundCommissionAmount;
		refundCommissionAmountVat = builder.refundCommissionAmountVat;
		subscriptionAmount = builder.subscriptionAmount;
		totalChargedAmount = builder.totalChargedAmount;
		totalChargedAmountVat = builder.totalChargedAmountVat;
		shopOperatorInternalId = builder.shopOperatorInternalId;
		shopIdentificationNumber = builder.shopIdentificationNumber;
		totalManualInvoiceAmount = builder.totalManualInvoiceAmount;
		totalManualInvoiceAmountVat = builder.totalManualInvoiceAmountVat;
		totalManualCreditAmount = builder.totalManualCreditAmount;
		totalManualCreditAmountVat = builder.totalManualCreditAmountVat;
		currencyIsoCode = builder.currencyIsoCode;
		paymentInfoType = builder.paymentInfoType;
		destinationToken = builder.destinationToken;
		hyperwalletProgram = builder.hyperwalletProgram;
	}

	@SuppressWarnings("java:S3740")
	public static Builder builder() {
		return new Builder() {
			@Override
			public Builder getThis() {
				return this;
			}
		};
	}

	@SuppressWarnings("java:S3740")
	public Builder toBuilder() {
		//@formatter:off
		return builder()
				.shopId(shopId)
				.startTime(startTime)
				.endTime(endTime)
				.shopName(shopName)
				.shopCorporateName(shopCorporateName)
				.shopAddressLastName(shopAddressLastName)
				.shopAddressFirstName(shopAddressFirstName)
				.shopAddressStreet1(shopAddressStreet1)
				.shopAddressStreet2(shopAddressStreet2)
				.shopAddressComplementary(shopAddressComplementary)
				.shopAddressZipCode(shopAddressZipCode)
				.shopAddressCity(shopAddressCity)
				.shopAddressCountry(shopAddressCountry)
				.shopAddressState(shopAddressState)
				.shopEmail(shopEmail)
				.shopIsProfessional(shopIsProfessional)
				.shopVatNumber(shopVatNumber)
				.dateCreated(dateCreated)
				.invoiceNumber(invoiceNumber)
				.paymentInfoOwner(paymentInfoOwner)
				.paymentInfoBankName(paymentInfoBankName)
				.paymentInfoBankStreet(paymentInfoBankStreet)
				.paymentInfoBankZip(paymentInfoBankZip)
				.paymentInfoBankCity(paymentInfoBankCity)
				.paymentInfoBic(paymentInfoBic)
				.paymentInfoIban(paymentInfoIban)
				.orderAmount(orderAmount)
				.orderShippingAmount(orderShippingAmount)
				.orderCommissionAmount(orderCommissionAmount)
				.refundAmount(refundAmount)
				.refundShippingAmount(refundShippingAmount)
				.refundCommissionAmount(refundCommissionAmount)
				.refundCommissionAmountVat(refundCommissionAmountVat)
				.subscriptionAmount(subscriptionAmount)
				.totalChargedAmount(totalChargedAmount)
				.totalChargedAmountVat(totalChargedAmountVat)
				.shopOperatorInternalId(shopOperatorInternalId)
				.shopIdentificationNumber(shopIdentificationNumber)
				.totalManualInvoiceAmount(totalManualInvoiceAmount)
				.totalManualInvoiceAmountVat(totalManualInvoiceAmountVat)
				.totalManualCreditAmount(totalManualCreditAmount)
				.totalManualCreditAmountVat(totalManualCreditAmountVat)
				.currencyIsoCode(currencyIsoCode)
				.paymentInfoType(paymentInfoType)
				.destinationToken(destinationToken)
				.hyperwalletProgram(hyperwalletProgram);
		//@formatter:on
	}

	public abstract static class Builder<T extends Builder<T>> {

		private String shopId;

		private Date startTime;

		private Date endTime;

		private String shopName;

		private String shopCorporateName;

		private String shopAddressLastName;

		private String shopAddressFirstName;

		private String shopAddressStreet1;

		private String shopAddressStreet2;

		private String shopAddressComplementary;

		private String shopAddressZipCode;

		private String shopAddressCity;

		private String shopAddressCountry;

		private String shopAddressState;

		private String shopEmail;

		private boolean shopIsProfessional;

		private String shopVatNumber;

		private Date dateCreated;

		private String invoiceNumber;

		private String paymentInfoOwner;

		private String paymentInfoBankName;

		private String paymentInfoBankStreet;

		private String paymentInfoBankZip;

		private String paymentInfoBankCity;

		private String paymentInfoBic;

		private String paymentInfoIban;

		private Double orderAmount;

		private Double orderShippingAmount;

		private Double orderCommissionAmount;

		private Double refundAmount;

		private Double refundShippingAmount;

		private Double refundCommissionAmount;

		private Double refundCommissionAmountVat;

		private Double subscriptionAmount;

		private Double totalChargedAmount;

		private Double totalChargedAmountVat;

		private String shopOperatorInternalId;

		private String shopIdentificationNumber;

		private Double totalManualInvoiceAmount;

		private Double totalManualInvoiceAmountVat;

		private Double totalManualCreditAmount;

		private Double totalManualCreditAmountVat;

		private String currencyIsoCode;

		private String paymentInfoType;

		private String destinationToken;

		private String hyperwalletProgram;

		public abstract T getThis();

		public T shopId(final String shopId) {
			this.shopId = shopId;
			return getThis();
		}

		public T startTime(final Date startTime) {
			this.startTime = startTime;
			return getThis();
		}

		public T endTime(final Date endTime) {
			this.endTime = endTime;
			return getThis();
		}

		public T shopName(final String shopName) {
			this.shopName = shopName;
			return getThis();
		}

		public T shopCorporateName(final String shopCorporateName) {
			this.shopCorporateName = shopCorporateName;
			return getThis();
		}

		public T shopAddressLastName(final String shopAddressLastName) {
			this.shopAddressLastName = shopAddressLastName;
			return getThis();
		}

		public T shopAddressFirstName(final String shopAddressFirstName) {
			this.shopAddressFirstName = shopAddressFirstName;
			return getThis();
		}

		public T shopAddressStreet1(final String shopAddressStreet1) {
			this.shopAddressStreet1 = shopAddressStreet1;
			return getThis();
		}

		public T shopAddressStreet2(final String shopAddressStreet2) {
			this.shopAddressStreet2 = shopAddressStreet2;
			return getThis();
		}

		public T shopAddressComplementary(final String shopAddressComplementary) {
			this.shopAddressComplementary = shopAddressComplementary;
			return getThis();
		}

		public T shopAddressZipCode(final String shopAddressZipCode) {
			this.shopAddressZipCode = shopAddressZipCode;
			return getThis();
		}

		public T shopAddressCity(final String shopAddressCity) {
			this.shopAddressCity = shopAddressCity;
			return getThis();
		}

		public T shopAddressCountry(final String shopAddressCountry) {
			this.shopAddressCountry = shopAddressCountry;
			return getThis();
		}

		public T shopAddressState(final String shopAddressState) {
			this.shopAddressState = shopAddressState;
			return getThis();
		}

		public T shopEmail(final String shopEmail) {
			this.shopEmail = shopEmail;
			return getThis();
		}

		public T shopIsProfessional(final boolean shopIsProfessional) {
			this.shopIsProfessional = shopIsProfessional;
			return getThis();
		}

		public T shopVatNumber(final String shopVatNumber) {
			this.shopVatNumber = shopVatNumber;
			return getThis();
		}

		public T dateCreated(final Date dateCreated) {
			this.dateCreated = dateCreated;
			return getThis();
		}

		public T invoiceNumber(final String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
			return getThis();
		}

		public T paymentInfoOwner(final String paymentInfoOwner) {
			this.paymentInfoOwner = paymentInfoOwner;
			return getThis();
		}

		public T paymentInfoBankName(final String paymentInfoBankName) {
			this.paymentInfoBankName = paymentInfoBankName;
			return getThis();
		}

		public T paymentInfoBankStreet(final String paymentInfoBankStreet) {
			this.paymentInfoBankStreet = paymentInfoBankStreet;
			return getThis();
		}

		public T paymentInfoBankZip(final String paymentInfoBankZip) {
			this.paymentInfoBankZip = paymentInfoBankZip;
			return getThis();
		}

		public T paymentInfoBankCity(final String paymentInfoBankCity) {
			this.paymentInfoBankCity = paymentInfoBankCity;
			return getThis();
		}

		public T paymentInfoBic(final String paymentInfoBic) {
			this.paymentInfoBic = paymentInfoBic;
			return getThis();
		}

		public T paymentInfoIban(final String paymentInfoIban) {
			this.paymentInfoIban = paymentInfoIban;
			return getThis();
		}

		public T orderAmount(final Double orderAmount) {
			this.orderAmount = orderAmount;
			return getThis();
		}

		public T orderShippingAmount(final Double orderShippingAmount) {
			this.orderShippingAmount = orderShippingAmount;
			return getThis();
		}

		public T orderCommissionAmount(final Double orderCommissionAmount) {
			this.orderCommissionAmount = orderCommissionAmount;
			return getThis();
		}

		public T refundAmount(final Double refundAmount) {
			this.refundAmount = refundAmount;
			return getThis();
		}

		public T refundShippingAmount(final Double refundShippingAmount) {
			this.refundShippingAmount = refundShippingAmount;
			return getThis();
		}

		public T refundCommissionAmount(final Double refundCommissionAmount) {
			this.refundCommissionAmount = refundCommissionAmount;
			return getThis();
		}

		public T refundCommissionAmountVat(final Double refundCommissionAmountVat) {
			this.refundCommissionAmountVat = refundCommissionAmountVat;
			return getThis();
		}

		public T subscriptionAmount(final Double subscriptionAmount) {
			this.subscriptionAmount = subscriptionAmount;
			return getThis();
		}

		public T totalChargedAmount(final Double totalChargedAmount) {
			this.totalChargedAmount = totalChargedAmount;
			return getThis();
		}

		public T totalChargedAmountVat(final Double totalChargedAmountVat) {
			this.totalChargedAmountVat = totalChargedAmountVat;
			return getThis();
		}

		public T shopOperatorInternalId(final String shopOperatorInternalId) {
			this.shopOperatorInternalId = shopOperatorInternalId;
			return getThis();
		}

		public T shopIdentificationNumber(final String shopIdentificationNumber) {
			this.shopIdentificationNumber = shopIdentificationNumber;
			return getThis();
		}

		public T totalManualInvoiceAmount(final Double totalManualInvoiceAmount) {
			this.totalManualInvoiceAmount = totalManualInvoiceAmount;
			return getThis();
		}

		public T totalManualInvoiceAmountVat(final Double totalManualInvoiceAmountVat) {
			this.totalManualInvoiceAmountVat = totalManualInvoiceAmountVat;
			return getThis();
		}

		public T totalManualCreditAmount(final Double totalManualCreditAmount) {
			this.totalManualCreditAmount = totalManualCreditAmount;
			return getThis();
		}

		public T totalManualCreditAmountVat(final Double totalManualCreditAmountVat) {
			this.totalManualCreditAmountVat = totalManualCreditAmountVat;
			return getThis();
		}

		public T currencyIsoCode(final String currencyIsoCode) {
			this.currencyIsoCode = currencyIsoCode;
			return getThis();
		}

		public T paymentInfoType(final String paymentInfoType) {
			this.paymentInfoType = paymentInfoType;
			return getThis();
		}

		public T destinationToken(final String destinationToken) {
			this.destinationToken = destinationToken;
			return getThis();
		}

		public T destinationToken(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklStringCustomFieldValue(fields, HYPERWALLET_BANK_ACCOUNT_TOKEN)
					.ifPresent(retrievedToken -> destinationToken = retrievedToken);

			return getThis();
		}

		public T hyperwalletProgram(final String hyperwalletProgram) {
			this.hyperwalletProgram = hyperwalletProgram;
			return getThis();
		}

		public T hyperwalletProgram(final List<MiraklAdditionalFieldValue> fields) {
			getMiraklSingleValueListCustomFieldValue(fields, HYPERWALLET_PROGRAM)
					.ifPresent(retrievedHyperwalletProgram -> this.hyperwalletProgram = retrievedHyperwalletProgram);

			return getThis();
		}

		public AccountingDocumentModel build() {
			return new AccountingDocumentModel(this);
		}

		private Optional<String> getMiraklStringCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			//@formatter:off
			return fields.stream()
					.filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
			//@formatter:on
		}

		private Optional<String> getMiraklSingleValueListCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
				final String customFieldCode) {
			//@formatter:off
			return fields.stream()
					.filter(field -> field.getCode().equals(customFieldCode))
					.filter(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::isInstance)
					.map(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::cast).findAny()
					.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
			//@formatter:on
		}

	}

}
