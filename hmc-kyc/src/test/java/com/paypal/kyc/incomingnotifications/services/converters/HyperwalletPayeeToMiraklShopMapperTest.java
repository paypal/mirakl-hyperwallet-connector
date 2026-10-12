package com.paypal.kyc.incomingnotifications.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HyperwalletPayeeToMiraklShopMapperTest {

	@Test
	void apply_shouldMapLegacyBusinessNamesAndContactOntoTheShopUpdate() {
		final MiraklUpdateShop shop = new MiraklUpdateShop();
		final KYCUserStatusNotificationBodyModel notification = businessNotification("Shop DBA", "Legal Entity Ltd");

		final boolean applied = HyperwalletPayeeToMiraklShopMapper.apply(shop, notification, false);

		assertThat(applied).isTrue();
		assertThat(shop.getName()).isEqualTo("Shop DBA");
		assertThat(shop.getDefaultBillingInformation().getCorporateInformation().getCompanyRegistrationName())
			.isEqualTo("Legal Entity Ltd");
		assertThat(shop.getDefaultBillingInformation().getCorporateInformation().getCompanyRegistrationNumber())
			.isEqualTo("REG-1");
		assertThat(shop.getContactInformation().getEmail()).isEqualTo("seller@example.com");
		assertThat(shop.getContactInformation().getStreet1()).isEqualTo("30 King Street");
		assertThat(shop.getContactInformation().getCity()).isEqualTo("Valencia");
		assertThat(shop.getContactInformation().getCountry()).isEqualTo("ES");
		assertThat(shop.getContactInformation().getZipCode()).isEqualTo("46001");
	}

	@Test
	void apply_shouldSwapBusinessNamesWhenNewUserMappingIsEnabled() {
		final MiraklUpdateShop shop = new MiraklUpdateShop();
		final KYCUserStatusNotificationBodyModel notification = businessNotification("Legal Entity Ltd", "Shop DBA");

		HyperwalletPayeeToMiraklShopMapper.apply(shop, notification, true);

		assertThat(shop.getName()).isEqualTo("Shop DBA");
		assertThat(shop.getDefaultBillingInformation().getCorporateInformation().getCompanyRegistrationName())
			.isEqualTo("Legal Entity Ltd");
	}

	@Test
	void apply_shouldMapIndividualShopNameAndContactWithoutALegalEntity() {
		final MiraklUpdateShop shop = new MiraklUpdateShop();
		final KYCUserStatusNotificationBodyModel notification = KYCUserStatusNotificationBodyModel.builder()
			.profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
			.businessName("Jane Store")
			.firstName("Jane")
			.lastName("Seller")
			.phoneNumber("+34920124568")
			.mobileNumber("+34656201324")
			.build();

		HyperwalletPayeeToMiraklShopMapper.apply(shop, notification, true);

		assertThat(shop.getName()).isEqualTo("Jane Store");
		assertThat(shop.getContactInformation().getFirstname()).isEqualTo("Jane");
		assertThat(shop.getContactInformation().getLastname()).isEqualTo("Seller");
		assertThat(shop.getContactInformation().getPhone()).isEqualTo("+34920124568");
		assertThat(shop.getContactInformation().getPhoneSecondary()).isEqualTo("+34656201324");
		assertThat(shop.getDefaultBillingInformation()).isNull();
	}

	@Test
	void apply_shouldLeaveTheShopUntouchedWhenTheWebhookHasNoProfileFields() {
		final MiraklUpdateShop shop = new MiraklUpdateShop();
		final KYCUserStatusNotificationBodyModel notification = KYCUserStatusNotificationBodyModel.builder()
			.profileType(HyperwalletUser.ProfileType.BUSINESS)
			.build();

		final boolean applied = HyperwalletPayeeToMiraklShopMapper.apply(shop, notification, false);

		assertThat(applied).isFalse();
		assertThat(shop.getName()).isNull();
		assertThat(shop.getContactInformation()).isNull();
		assertThat(shop.getDefaultBillingInformation()).isNull();
	}

	private static KYCUserStatusNotificationBodyModel businessNotification(final String businessName,
			final String businessOperatingName) {
		return KYCUserStatusNotificationBodyModel.builder()
			.profileType(HyperwalletUser.ProfileType.BUSINESS)
			.businessName(businessName)
			.businessOperatingName(businessOperatingName)
			.businessRegistrationId("REG-1")
			.email("seller@example.com")
			.addressLine1("30 King Street")
			.city("Valencia")
			.country("ES")
			.postalCode("46001")
			.build();
	}

}
