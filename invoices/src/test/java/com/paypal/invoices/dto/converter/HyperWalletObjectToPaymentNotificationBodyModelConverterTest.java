package com.paypal.invoices.dto.converter;

import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.paymentnotifications.converter.HyperWalletObjectToPaymentNotificationBodyModelConverter;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToPaymentNotificationBodyModelConverterTest {

	@InjectMocks
	private HyperWalletObjectToPaymentNotificationBodyModelConverter testObj;

	private static final String TOKEN = "Token";

	private static final String STATUS = "Status";

	private static final String AMOUNT = "Amount";

	private static final String CURRENCY = "Currency";

	private static final String CLIENT_PAYMENT_ID = "ClientPaymentId";

	private static final String NOTES = "Notes";

	private static final String PURPOSE = "Purpose";

	private static final String RELEASE_ON = "ReleaseOn";

	private static final String EXPIRES_ON = "ExpiresOn";

	private static final String DESTINATION_TOKEN = "DestinationToken";

	@Test
	void convert_shouldTransformHyperWalletWebhookNotificationToInvoiceNotificationModel_whenDetailsIsJSonObject()
			throws JSONException {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		final LocalDateTime now = TimeMachine.now();
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final LinkedHashMap<String, String> hyperwalletPaymentBodyNotification = createHyperWalletPaymentBodyNotification(
				nowAsDate);

		final PaymentNotificationBodyModel result = testObj.convert(hyperwalletPaymentBodyNotification);

		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("token", TOKEN)
				.hasFieldOrPropertyWithValue("status", STATUS)
				.hasFieldOrPropertyWithValue("createdOn", nowAsDate.toString())
				.hasFieldOrPropertyWithValue("amount", AMOUNT)
				.hasFieldOrPropertyWithValue("currency", CURRENCY)
				.hasFieldOrPropertyWithValue("clientPaymentId", CLIENT_PAYMENT_ID)
				.hasFieldOrPropertyWithValue("notes", NOTES)
				.hasFieldOrPropertyWithValue("purpose", PURPOSE)
				.hasFieldOrPropertyWithValue("releaseOn", RELEASE_ON)
				.hasFieldOrPropertyWithValue("expiresOn", EXPIRES_ON)
				.hasFieldOrPropertyWithValue("destinationToken", DESTINATION_TOKEN);
		//@formatter:on
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookNotificationToInvoiceNotificationModel_whenDetailsIsAnIncompleteJSonObject()
			throws JSONException {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		final LocalDateTime now = TimeMachine.now();
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());

		final LinkedHashMap<String, String> hyperwalletPaymentIncompleteNotification = createIncompleteJsonObject(
				nowAsDate);

		final PaymentNotificationBodyModel result = testObj.convert(hyperwalletPaymentIncompleteNotification);

		//@formatter:off
		assertThat(result).hasFieldOrPropertyWithValue("status", STATUS)
				.hasFieldOrPropertyWithValue("createdOn", nowAsDate.toString())
				.hasFieldOrPropertyWithValue("amount", AMOUNT)
				.hasFieldOrPropertyWithValue("clientPaymentId", CLIENT_PAYMENT_ID)
				.hasFieldOrPropertyWithValue("notes", NOTES)
				.hasFieldOrPropertyWithValue("purpose", PURPOSE)
				.hasFieldOrPropertyWithValue("releaseOn", RELEASE_ON)
				.hasFieldOrPropertyWithValue("expiresOn", EXPIRES_ON);
		//@formatter:on
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookNotificationToInvoiceNotificationModel_whenObjectIsNotJSonObject()
			throws JSONException {
		final PaymentNotificationBodyModel result = testObj.convert(new Object());
		assertThat(result).isNull();
	}

	private LinkedHashMap<String, String> createIncompleteJsonObject(final Date nowAsDate) {
		final LinkedHashMap<String, String> detailInfo = new LinkedHashMap<>();
		detailInfo.put("status", STATUS);
		detailInfo.put("createdOn", nowAsDate.toString());
		detailInfo.put("amount", AMOUNT);
		detailInfo.put("clientPaymentId", CLIENT_PAYMENT_ID);
		detailInfo.put("notes", NOTES);
		detailInfo.put("releaseOn", RELEASE_ON);
		detailInfo.put("expiresOn", EXPIRES_ON);
		detailInfo.put("purpose", PURPOSE);
		return detailInfo;
	}

	private LinkedHashMap<String, String> createHyperWalletPaymentBodyNotification(final Date nowAsDate) {
		final LinkedHashMap<String, String> detailInfo = new LinkedHashMap<>();
		detailInfo.put("token", TOKEN);
		detailInfo.put("status", STATUS);
		detailInfo.put("createdOn", nowAsDate.toString());
		detailInfo.put("amount", AMOUNT);
		detailInfo.put("currency", CURRENCY);
		detailInfo.put("clientPaymentId", CLIENT_PAYMENT_ID);
		detailInfo.put("notes", NOTES);
		detailInfo.put("releaseOn", RELEASE_ON);
		detailInfo.put("expiresOn", EXPIRES_ON);
		detailInfo.put("destinationToken", DESTINATION_TOKEN);
		detailInfo.put("purpose", PURPOSE);
		return detailInfo;
	}

}
