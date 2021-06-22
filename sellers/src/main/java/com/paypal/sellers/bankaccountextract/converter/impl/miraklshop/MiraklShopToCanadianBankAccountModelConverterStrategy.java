package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklCanadianBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;
import com.paypal.sellers.bankaccountextract.model.CanadianBankAccountModel;
import com.paypal.sellers.bankaccountextract.model.TransferType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert from {@link MiraklShop} to {@link CanadianBankAccountModel}
 */
@Slf4j
@Service
public class MiraklShopToCanadianBankAccountModelConverterStrategy implements Strategy<MiraklShop, BankAccountModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountModel execute(final MiraklShop source) {
		final MiraklPaymentInformation paymentInformation = source.getPaymentInformation();
		final MiraklCanadianBankAccountInformation miraklCanadianBankAccountInformation = (MiraklCanadianBankAccountInformation) paymentInformation;
		final MiraklContactInformation contactInformation = source.getContactInformation();

		//@formatter:off
		return CanadianBankAccountModel.builder()
				.bankId(miraklCanadianBankAccountInformation.getInstitutionNumber())
				.branchId(miraklCanadianBankAccountInformation.getTransitNumber())
				.transferMethodCountry(contactInformation.getCountry())
				.transferMethodCurrency(source.getCurrencyIsoCode().name())
				.transferType(TransferType.BANK_ACCOUNT)
				.type(BankAccountType.CANADIAN)
				.bankAccountNumber(miraklCanadianBankAccountInformation.getBankAccountNumber())
				.businessName(Optional.ofNullable(source.getProfessionalInformation())
						.map(MiraklProfessionalInformation::getCorporateName)
						.orElse(null))
				.firstName(contactInformation.getFirstname())
				.lastName(contactInformation.getLastname())
				.country(contactInformation.getCountry())
				.city(miraklCanadianBankAccountInformation.getBankCity())
				.stateProvince(source.getAdditionalFieldValues())
				.postalCode(miraklCanadianBankAccountInformation.getBankZip())
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
				&& source.getPaymentInformation() instanceof MiraklCanadianBankAccountInformation;
	}

}
