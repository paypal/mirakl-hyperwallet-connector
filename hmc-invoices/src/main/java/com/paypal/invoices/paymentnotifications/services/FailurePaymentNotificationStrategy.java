package com.paypal.invoices.paymentnotifications.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Class that handles the error on payment notifications
 */
@Service
@Slf4j
public class FailurePaymentNotificationStrategy implements Strategy<PaymentNotificationBodyModel, Void> {

	public static final String EMAILMSG_WITHSHOPINFO_SUBJECT = "Problem while processing payment [%s] of shop %s with id [%s]";

	public static final String EMAILMSG_WITHSHOPINFO_BODY = "A problem has been detected while processing the payment corresponding to the invoice [%s] of the shop %s with id [%s].%n"
			+ "The status received for the payment is [%s].%n"
			+ "For more information please consult your Hyperwallet dashboard.";

	public static final String EMAILMSG_NOSHOPINFO_SUBJECT = "Problem while processing payment [%s]";

	public static final String EMAILMSG_NOSHOPINFO_BODY = "A problem has been detected while processing the payment corresponding to the invoice [%s].%n"
			+ "The status received for the payment is [%s].%n"
			+ "For more information please consult your Hyperwallet dashboard.";

	@Resource
	private PaymentNotificationConfig paymentNotificationConfig;

	@Resource
	private MailNotificationUtil mailNotificationUtil;

	@Resource
	private UserHyperwalletSDKService userHyperwalletSDKService;

	/**
	 * Executes the business logic based on the content of
	 * {@code paymentNotificationBodyModel} and returns a {@link Void} class based on a
	 * set of strategies
	 * @param paymentNotificationBodyModel the paymentNotificationBodyModel object of type
	 * {@link PaymentNotificationBodyModel}
	 * @return the converted object of type {@link Void}
	 */
	@Override
	public Void execute(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		final HyperwalletUser hyperwalletUser = getHyperwalletUserInfo(paymentNotificationBodyModel);
		if (hyperwalletUser != null) {
			sendErrorMailWithShopInfo(paymentNotificationBodyModel, hyperwalletUser);
		}
		else {
			sendErrorMailWithoutShopInfo(paymentNotificationBodyModel);
		}

		return null;
	}

	private void sendErrorMailWithShopInfo(final PaymentNotificationBodyModel paymentNotificationBodyModel,
			final HyperwalletUser hyperwalletUser) {
		//@formatter:off
		final String shopName = StringUtils.hasText(hyperwalletUser.getBusinessName()) ? hyperwalletUser.getBusinessName() :
				"%s %s".formatted(hyperwalletUser.getFirstName(), hyperwalletUser.getLastName());
		mailNotificationUtil.sendPlainTextEmail(
				String.format(EMAILMSG_WITHSHOPINFO_SUBJECT,
						paymentNotificationBodyModel.getClientPaymentId(), shopName, hyperwalletUser.getClientUserId()),
				String.format(EMAILMSG_WITHSHOPINFO_BODY,
						paymentNotificationBodyModel.getClientPaymentId(), shopName, hyperwalletUser.getClientUserId(),
						paymentNotificationBodyModel.getStatus()));
		//@formatter:on
	}

	private void sendErrorMailWithoutShopInfo(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		//@formatter:off
		mailNotificationUtil.sendPlainTextEmail(
				String.format(EMAILMSG_NOSHOPINFO_SUBJECT, paymentNotificationBodyModel.getClientPaymentId()),
				String.format(EMAILMSG_NOSHOPINFO_BODY, paymentNotificationBodyModel.getClientPaymentId(),
						paymentNotificationBodyModel.getStatus()));
		//@formatter:on
	}

	/**
	 * Checks whether the strategy must be executed based on the
	 * {@code paymentNotificationBodyModel}
	 * @param paymentNotificationBodyModel the paymentNotificationBodyModel object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		return Objects.nonNull(paymentNotificationBodyModel)
				&& paymentNotificationConfig.getFailureStatuses().contains(paymentNotificationBodyModel.getStatus());
	}

	HyperwalletUser getHyperwalletUserInfo(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		final Hyperwallet hyperwallet = userHyperwalletSDKService
				.getHyperwalletInstanceByProgramToken(paymentNotificationBodyModel.getProgramToken());

		try {
			return hyperwallet.getUser(paymentNotificationBodyModel.getDestinationToken());
		}
		catch (final HyperwalletException e) {
			log.warn(String.format("Error while retrieving Hyperwallet User info for destination token[%s].%n%s",
					paymentNotificationBodyModel.getDestinationToken(), HyperwalletLoggingErrorsUtil.stringify(e)), e);

			return null;
		}
	}

}
