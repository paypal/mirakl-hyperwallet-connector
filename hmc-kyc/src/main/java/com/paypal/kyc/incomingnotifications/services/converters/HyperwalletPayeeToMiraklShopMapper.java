package com.paypal.kyc.incomingnotifications.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * Writes a verified Hyperwallet payee back onto the Mirakl shop update that already
 * carries the KYC status. Name fields follow the reverse of
 * {@code SellerModelToHyperWalletUserConverter}: the new-user-mapping toggle decides
 * which Hyperwallet name is the shop name and which is the legal entity name.
 */
public final class HyperwalletPayeeToMiraklShopMapper {

	private HyperwalletPayeeToMiraklShopMapper() {
	}

	public static boolean apply(final MiraklUpdateShop shop, final KYCUserStatusNotificationBodyModel notification,
			final boolean newUserMappingEnabled) {
		if (notification == null) {
			return false;
		}
		final boolean business = HyperwalletUser.ProfileType.BUSINESS.equals(notification.getProfileType());
		final NamePair names = names(notification, business, newUserMappingEnabled);
		final boolean shopNameApplied = set(shop::setName, names.shopName());
		final boolean contactApplied = applyContact(shop, notification);
		final boolean legalEntityApplied = business
				&& applyLegalEntity(shop, names.legalName(), notification.getBusinessRegistrationId());
		return shopNameApplied || contactApplied || legalEntityApplied;
	}

	private static NamePair names(final KYCUserStatusNotificationBodyModel notification, final boolean business,
			final boolean newUserMappingEnabled) {
		if (business && newUserMappingEnabled) {
			return new NamePair(notification.getBusinessOperatingName(), notification.getBusinessName());
		}
		if (business) {
			return new NamePair(notification.getBusinessName(), notification.getBusinessOperatingName());
		}
		return new NamePair(notification.getBusinessName(), null);
	}

	private static boolean applyContact(final MiraklUpdateShop shop,
			final KYCUserStatusNotificationBodyModel notification) {
		final MiraklContactInformation contact = new MiraklContactInformation();
		final boolean changed = set(contact::setEmail, notification.getEmail())
				| set(contact::setFirstname, notification.getFirstName())
				| set(contact::setLastname, notification.getLastName())
				| set(contact::setPhone, notification.getPhoneNumber())
				| set(contact::setPhoneSecondary, notification.getMobileNumber())
				| set(contact::setStreet1, notification.getAddressLine1())
				| set(contact::setStreet2, notification.getAddressLine2())
				| set(contact::setCity, notification.getCity())
				| set(contact::setState, notification.getStateProvince())
				| set(contact::setCountry, notification.getCountry())
				| set(contact::setZipCode, notification.getPostalCode());
		if (changed) {
			shop.setContactInformation(contact);
		}
		return changed;
	}

	private static boolean applyLegalEntity(final MiraklUpdateShop shop, final String legalName,
			final String registrationId) {
		final MiraklDefaultBillingInformation.CorporateInformation corporate = new MiraklDefaultBillingInformation.CorporateInformation();
		final boolean changed = set(corporate::setCompanyRegistrationName, legalName)
				| set(corporate::setCompanyRegistrationNumber, registrationId);
		if (!changed) {
			return false;
		}
		final MiraklDefaultBillingInformation billing = new MiraklDefaultBillingInformation();
		billing.setCorporateInformation(corporate);
		shop.setDefaultBillingInformation(billing);
		return true;
	}

	private static boolean set(final Consumer<String> setter, final String value) {
		if (StringUtils.isBlank(value)) {
			return false;
		}
		setter.accept(value);
		return true;
	}

	private record NamePair(String shopName, String legalName) {
	}

}
