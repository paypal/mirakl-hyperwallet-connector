package com.paypal.sellers.bankaccountextraction.services.converters.mirakl;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklIbanBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.IBANBankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.TransferType;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyInfo;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert from {@link MiraklShop} to {@link IBANBankAccountModel}
 */
@Slf4j
@Service
public class MiraklShopToIBANBankAccountModelConverterStrategy implements Strategy<MiraklShop, BankAccountModel> {

	private final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver;

	public MiraklShopToIBANBankAccountModelConverterStrategy(
			final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver) {
		this.hyperwalletBankAccountCurrencyResolver = hyperwalletBankAccountCurrencyResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBANBankAccountModel execute(@NonNull final MiraklShop source) {
		final MiraklPaymentInformation paymentInformation = source.getPaymentInformation();
		final MiraklIbanBankAccountInformation miraklIbanBankAccountInformation = (MiraklIbanBankAccountInformation) paymentInformation;
		final MiraklContactInformation contactInformation = source.getContactInformation();

		final String bankCountryIsoCode = extractCountryFromIban(source, miraklIbanBankAccountInformation);
		final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo = hyperwalletBankAccountCurrencyResolver
				.getCurrencyForCountry(BankAccountType.IBAN.name(), bankCountryIsoCode,
						source.getCurrencyIsoCode().name());

		//@formatter:off
		return IBANBankAccountModel.builder()
				.transferMethodCountry(bankCountryIsoCode)
				.transferMethodCurrency(source.getCurrencyIsoCode().name())
				.transferMethodCurrency(hyperwalletBankAccountCurrencyInfo.getCurrency())
				.transferType(TransferType.BANK_ACCOUNT)
				.type(BankAccountType.IBAN)
				.bankBic(miraklIbanBankAccountInformation.getBic())
				.bankAccountNumber(miraklIbanBankAccountInformation.getIban())
				.businessName(Optional.ofNullable(source.getDefaultBillingInformation())
						.map(MiraklDefaultBillingInformation::getCorporateInformation)
						.map(MiraklDefaultBillingInformation.CorporateInformation::getCompanyRegistrationName)
						.orElse(null))
				.firstName(contactInformation.getFirstname())
				.lastName(contactInformation.getLastname())
				.country(contactInformation.getCountry())
				.addressLine1(contactInformation.getStreet1())
				.addressLine2(Optional.ofNullable(contactInformation.getStreet2())
						.orElse(StringUtils.EMPTY))
				.city(miraklIbanBankAccountInformation.getBankCity())
				.stateProvince(source.getAdditionalFieldValues())
				.token(source.getAdditionalFieldValues())
				.hyperwalletProgram(source.getAdditionalFieldValues())
				.build();
		//@formatter:on
	}

	private String extractCountryFromIban(final MiraklShop source, final MiraklIbanBankAccountInformation ibanInfo) {
		final String iban = ibanInfo.getIban();
		if (StringUtils.isBlank(iban) || iban.length() < 2) {
			throw new IllegalStateException("IBAN invalid on shop: %s".formatted(source.getId()));
		}

		return iban.substring(0, 2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final MiraklShop source) {
		return Objects.nonNull(source.getPaymentInformation())
				&& source.getPaymentInformation() instanceof MiraklIbanBankAccountInformation;
	}

}
