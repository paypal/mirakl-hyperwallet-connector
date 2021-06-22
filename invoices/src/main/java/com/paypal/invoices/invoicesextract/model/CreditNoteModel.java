package com.paypal.invoices.invoicesextract.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CreditNoteModel extends AccountingDocumentModel {

	private final Double creditAmount;

	public CreditNoteModel(final Builder builder) {
		super(builder);
		creditAmount = builder.creditAmount;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public Builder toBuilder() {
		//@formatter:off
        return CreditNoteModel.builder()
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
                .creditAmount(creditAmount);
        //@formatter:on
	}

	public static class Builder extends AccountingDocumentModel.Builder<Builder> {

		private Double creditAmount;

		public Builder creditAmount(final Double creditAmount) {
			this.creditAmount = creditAmount;
			return this;
		}

		@Override
		public Builder getThis() {
			return this;
		}

		@Override
		public CreditNoteModel build() {
			return new CreditNoteModel(this);
		}

	}

}
