package com.paypal.sellers.bankaccountextraction.services.converters.mirakl;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklCanadianBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.CanadianBankAccountModel;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyInfo;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert from {@link MiraklShop} to {@link CanadianBankAccountModel}
 */
@Slf4j
@Service
public class MiraklShopToCanadianBankAccountModelConverterStrategy implements Strategy<MiraklShop, BankAccountModel> {

	private final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver;

	public MiraklShopToCanadianBankAccountModelConverterStrategy(
			final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver) {
		this.hyperwalletBankAccountCurrencyResolver = hyperwalletBankAccountCurrencyResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountModel execute(final MiraklShop source) {
		final MiraklPaymentInformation paymentInformation = source.getPaymentInformation();
		final MiraklCanadianBankAccountInformation miraklCanadianBankAccountInformation = (MiraklCanadianBankAccountInformation) paymentInformation;
		final MiraklContactInformation contactInformation = source.getContactInformation();

		final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo = hyperwalletBankAccountCurrencyResolver
				.getCurrencyForCountry(BankAccountType.CANADIAN.name(), Locale.CANADA.getCountry(),
						source.getCurrencyIsoCode().name());

		//@formatter:off
		return CanadianBankAccountModel.builder()
				.bankId(miraklCanadianBankAccountInformation.getInstitutionNumber())
				.branchId(miraklCanadianBankAccountInformation.getTransitNumber())
				.transferMethodCountry(Locale.CANADA.getCountry())
				.transferMethodCurrency(hyperwalletBankAccountCurrencyInfo.getCurrency())
				.transferType(hyperwalletBankAccountCurrencyInfo.getTransferType())
				.type(BankAccountType.CANADIAN)
				.bankAccountNumber(miraklCanadianBankAccountInformation.getBankAccountNumber())
				.businessName(Optional.ofNullable(source.getDefaultBillingInformation())
						.map(MiraklDefaultBillingInformation::getCorporateInformation)
						.map(MiraklDefaultBillingInformation.CorporateInformation::getCompanyRegistrationName)
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
