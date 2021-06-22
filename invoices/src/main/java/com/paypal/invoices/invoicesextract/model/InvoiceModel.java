package com.paypal.invoices.invoicesextract.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InvoiceModel extends AccountingDocumentModel {

	private final Double transferAmount;

	private final Double subscriptionAmountVat;

	private final Double orderCommissionAmountVat;

	public InvoiceModel(final Builder builder) {
		super(builder);
		transferAmount = builder.transferAmount;
		subscriptionAmountVat = builder.subscriptionAmountVat;
		orderCommissionAmountVat = builder.orderCommissionAmountVat;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public Builder toBuilder() {
		//@formatter:off
		return InvoiceModel.builder()
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
				.hyperwalletProgram(hyperwalletProgram)
				.transferAmount(transferAmount)
				.subscriptionAmountVat(subscriptionAmountVat)
				.orderCommissionAmountVat(orderCommissionAmountVat);
		//@formatter:on
	}

	public static class Builder extends AccountingDocumentModel.Builder<Builder> {

		private Double transferAmount;

		private Double subscriptionAmountVat;

		private Double orderCommissionAmountVat;

		public Builder transferAmount(final Double transferAmount) {
			this.transferAmount = transferAmount;
			return this;
		}

		public Builder subscriptionAmountVat(final Double subscriptionAmountVat) {
			this.subscriptionAmountVat = subscriptionAmountVat;
			return this;
		}

		public Builder orderCommissionAmountVat(final Double orderCommissionAmountVat) {
			this.orderCommissionAmountVat = orderCommissionAmountVat;
			return this;
		}

		@Override
		public Builder getThis() {
			return this;
		}

		@Override
		public InvoiceModel build() {
			return new InvoiceModel(this);
		}

	}

}
