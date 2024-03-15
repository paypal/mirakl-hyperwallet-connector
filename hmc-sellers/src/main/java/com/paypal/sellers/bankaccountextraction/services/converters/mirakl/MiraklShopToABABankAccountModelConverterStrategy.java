package com.paypal.sellers.bankaccountextraction.services.converters.mirakl;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.bank.MiraklAbaBankAccountInformation;
import com.mirakl.client.mmp.domain.shop.bank.MiraklPaymentInformation;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.bankaccountextraction.model.ABABankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountPurposeType;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyInfo;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert from {@link MiraklShop} to {@link ABABankAccountModel}
 */
@Slf4j
@Service
public class MiraklShopToABABankAccountModelConverterStrategy implements Strategy<MiraklShop, BankAccountModel> {

	private final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver;

	public MiraklShopToABABankAccountModelConverterStrategy(
			final HyperwalletBankAccountCurrencyResolver hyperwalletBankAccountCurrencyResolver) {
		this.hyperwalletBankAccountCurrencyResolver = hyperwalletBankAccountCurrencyResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ABABankAccountModel execute(@NonNull final MiraklShop source) {
		final MiraklPaymentInformation paymentInformation = source.getPaymentInformation();

		final MiraklAbaBankAccountInformation miraklAbaBankAccountInformation = (MiraklAbaBankAccountInformation) paymentInformation;
		final MiraklContactInformation contactInformation = source.getContactInformation();

		final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo = hyperwalletBankAccountCurrencyResolver
				.getCurrencyForCountry(BankAccountType.ABA.name(), Locale.US.getCountry(),
						source.getCurrencyIsoCode().name());

		//@formatter:off
		return ABABankAccountModel.builder()
				.transferMethodCountry(Locale.US.getCountry())
				.branchId(miraklAbaBankAccountInformation.getRoutingNumber())
				.bankAccountPurpose(BankAccountPurposeType.CHECKING.name())
				.transferMethodCurrency(hyperwalletBankAccountCurrencyInfo.getCurrency())
				.transferType(hyperwalletBankAccountCurrencyInfo.getTransferType())
				.type(BankAccountType.ABA)
				.bankAccountNumber(miraklAbaBankAccountInformation.getBankAccountNumber())
				.businessName(Optional.ofNullable(source.getDefaultBillingInformation())
						.map(MiraklDefaultBillingInformation::getCorporateInformation)
						.map(MiraklDefaultBillingInformation.CorporateInformation::getCompanyRegistrationName)
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
