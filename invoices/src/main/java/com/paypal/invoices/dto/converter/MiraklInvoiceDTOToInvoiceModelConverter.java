package com.paypal.invoices.dto.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.dto.MiraklInvoiceMockDTO;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MiraklInvoiceDTOToInvoiceModelConverter implements Converter<MiraklInvoiceMockDTO, InvoiceModel> {

	@Override
	public InvoiceModel convert(final MiraklInvoiceMockDTO source) {
		if (Objects.isNull(source)) {
			return null;
		}
		// @formatter:off
		return InvoiceModel.builder()
				.hyperwalletProgram(source.getHyperwalletProgram())
				.shopId(source.getShopId())
				.shopName(source.getShopName())
				.shopCorporateName(source.getShopCorporateName())
				.shopAddressLastName(source.getShopAddressLastName())
				.shopAddressFirstName(source.getShopAddressFirstName())
				.shopAddressStreet1(source.getShopAddressStreet1())
				.shopAddressStreet2(source.getShopAddressStreet2())
				.shopAddressComplementary(source.getShopAddressComplementary())
				.shopAddressZipCode(source.getShopAddressZipCode())
				.shopAddressCity(source.getShopAddressCity())
				.shopAddressCountry(source.getShopAddressCountry())
				.shopAddressState(source.getShopAddressState())
				.shopEmail(source.getShopEmail())
				.shopIsProfessional(source.isShopIsProfessional())
				.shopVatNumber(source.getShopVatNumber())
				.dateCreated(source.getDateCreated())
				.startTime(source.getStartTime())
				.endTime(source.getEndTime())
				.invoiceNumber(source.getInvoiceNumber())
				.paymentInfoOwner(source.getPaymentInfoOwner())
				.paymentInfoBankName(source.getPaymentInfoBankName())
				.paymentInfoBankStreet(source.getPaymentInfoBankStreet())
				.paymentInfoBankZip(source.getPaymentInfoBankZip())
				.paymentInfoBankCity(source.getPaymentInfoBankCity())
				.paymentInfoBic(source.getPaymentInfoBic())
				.paymentInfoIban(source.getPaymentInfoIban())
				.orderAmount(source.getOrderAmount())
				.orderShippingAmount(source.getOrderShippingAmount())
				.orderCommissionAmount(source.getOrderCommissionAmount())
				.orderCommissionAmountVat(source.getOrderCommissionAmountVat())
				.refundAmount(source.getRefundAmount())
				.refundShippingAmount(source.getRefundShippingAmount())
				.refundCommissionAmount(source.getRefundCommssionAmount())
				.refundCommissionAmountVat(source.getRefundCommissionAmountVat())
				.subscriptionAmount(source.getSubscriptionAmount())
				.subscriptionAmountVat(source.getSubscriptionAmountVat())
				.totalChargedAmount(source.getTotalChargedAmount())
				.totalChargedAmountVat(source.getTotalChargedAmountVat())
				.transferAmount(source.getTransferAmount())
				.shopOperatorInternalId(source.getShopOperatorInternalId())
				.shopIdentificationNumber(source.getShopIdentificationNumber())
				.totalManualInvoiceAmount(source.getTotalManualInvoiceAmount())
				.totalManualInvoiceAmountVat(source.getTotalManualInvoiceAmountVat())
				.totalManualCreditAmount(source.getTotalManualCreditAmount())
				.totalManualCreditAmountVat(source.getTotalManualCreditAmountVat())
				.transferAmountToOperator(source.getAmountTransferredToOperator())
				.currencyIsoCode(source.getCurrencyIsoCode())
				.paymentInfoType(source.getPaymentInfoType())
				.build();
		//@formatter:on
	}

}
