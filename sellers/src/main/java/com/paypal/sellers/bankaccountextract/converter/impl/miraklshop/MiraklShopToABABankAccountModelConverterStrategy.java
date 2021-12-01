package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklAbaBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.bankaccountextract.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert from {@link MiraklShop} to {@link ABABankAccountModel}
 */
@Slf4j
@Service
public class MiraklShopToABABankAccountModelConverterStrategy implements Strategy<MiraklShop, BankAccountModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ABABankAccountModel execute(@NonNull final MiraklShop source) {
		final MiraklPaymentInformation paymentInformation = source.getPaymentInformation();

		final MiraklAbaBankAccountInformation miraklAbaBankAccountInformation = (MiraklAbaBankAccountInformation) paymentInformation;
		final MiraklContactInformation contactInformation = source.getContactInformation();

		//@formatter:off
		return ABABankAccountModel.builder()
				.transferMethodCountry(contactInformation.getCountry())
				.branchId(miraklAbaBankAccountInformation.getRoutingNumber())
				.bankAccountPurpose(BankAccountPurposeType.CHECKING.name())
				.transferMethodCurrency(source.getCurrencyIsoCode().name())
				.transferType(TransferType.BANK_ACCOUNT)
				.type(BankAccountType.ABA)
				.bankAccountNumber(miraklAbaBankAccountInformation.getBankAccountNumber())
				.businessName(Optional.ofNullable(source.getProfessionalInformation())
						.map(MiraklProfessionalInformation::getCorporateName)
						.orElse(null))
				.firstName(contactInformation.getFirstname())
				.lastName(contactInformation.getLastname())
				.country(contactInformation.getCountry())
				.city(miraklAbaBankAccountInformation.getBankCity())
				.stateProvince(source.getAdditionalFieldValues())
				.postalCode(miraklAbaBankAccountInformation.getBankZip())
				.addressLine1(contactInformation.getStreet1())
				.addressLine2(Optional.ofNullable(contactInformation.getStreet2())
						.orElse(StringUtils.EMPTY))
				.token(source.getAdditionalFieldValues())
				.hyperwalletProgram(source.getAdditionalFieldValues())
				.build();
		//@formatter:on
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final MiraklShop source) {
		return Objects.nonNull(source.getPaymentInformation())
				&& source.getPaymentInformation() instanceof MiraklAbaBankAccountInformation;
	}

}
