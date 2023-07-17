package com.paypal.sellers.bankaccountextraction.services.converters.mirakl;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklUkBankAccountInformation;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.UKBankAccountModel;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyInfo;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert from {@link MiraklShop} to {@link UKBankAccountModel}
 */
@Service
public class MiraklShopToUKBankAccountModelConverterStrategy implements Strategy<MiraklShop, BankAccountModel> {

	private final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver;

	public MiraklShopToUKBankAccountModelConverterStrategy(
			final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver) {
		this.hyperwalletBankAccountCurrencyResolver = hyperwalletBankAccountCurrencyResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountModel execute(final MiraklShop source) {
		final MiraklPaymentInformation paymentInformation = source.getPaymentInformation();
		final MiraklUkBankAccountInformation miraklUkBankAccountInformation = (MiraklUkBankAccountInformation) paymentInformation;
		final MiraklContactInformation contactInformation = source.getContactInformation();

		final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo = hyperwalletBankAccountCurrencyResolver
				.getCurrencyForCountry(BankAccountType.UK.name(), Locale.UK.getCountry(),
						source.getCurrencyIsoCode().name());

		//@formatter:off
		return UKBankAccountModel.builder()
				.transferMethodCountry(Locale.UK.getCountry())
				.transferMethodCurrency(hyperwalletBankAccountCurrencyInfo.getCurrency())
				.transferType(hyperwalletBankAccountCurrencyInfo.getTransferType())
				.type(BankAccountType.UK)
				.bankAccountNumber(miraklUkBankAccountInformation.getBankAccountNumber())
				.bankAccountId(miraklUkBankAccountInformation.getBankSortCode())
				.businessName(Optional.ofNullable(source.getProfessionalInformation())
						.map(MiraklProfessionalInformation::getCorporateName)
						.orElse(null))
				.firstName(contactInformation.getFirstname())
				.lastName(contactInformation.getLastname())
				.country(contactInformation.getCountry())
				.addressLine1(contactInformation.getStreet1())
				.addressLine2(Optional.ofNullable(contactInformation.getStreet2())
						.orElse(StringUtils.EMPTY))
				.city(miraklUkBankAccountInformation.getBankCity())
				.stateProvince(source.getAdditionalFieldValues())
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
				&& source.getPaymentInformation() instanceof MiraklUkBankAccountInformation;
	}

}
