package com.paypal.sellers.stakeholdersextraction.services.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class HyperWalletUpdateBusinessStakeHolderServiceStrategy
		implements Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel> {

	private final Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverter;

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	private final MailNotificationUtil mailNotificationUtil;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public HyperWalletUpdateBusinessStakeHolderServiceStrategy(
			final Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverter,
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		this.businessStakeHolderModelHyperwalletBusinessStakeholderConverter = businessStakeHolderModelHyperwalletBusinessStakeholderConverter;
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * Executes the business logic based on the content of
	 * {@code businessStakeHolderModel} and returns a {@link BusinessStakeHolderModel}
	 * class based on a set of strategies
	 * @param businessStakeHolderModel the businessStakeHolderModel object of type
	 * {@link BusinessStakeHolderModel}
	 * @return the converted object of type {@link BusinessStakeHolderModel}
	 */
	@Override
	public BusinessStakeHolderModel execute(final BusinessStakeHolderModel businessStakeHolderModel) {
		final HyperwalletBusinessStakeholder hyperWalletBusinessStakeHolder = businessStakeHolderModelHyperwalletBusinessStakeholderConverter
				.convert(businessStakeHolderModel);
		try {

			log.info("Updating stakeholder [{}] for user [{}]", hyperWalletBusinessStakeHolder.getToken(),
					businessStakeHolderModel.getUserToken());

			final Hyperwallet hyperwallet = userHyperwalletSDKService
					.getHyperwalletInstanceByHyperwalletProgram(businessStakeHolderModel.getHyperwalletProgram());

			hyperwallet.updateBusinessStakeholder(businessStakeHolderModel.getUserToken(),
					hyperWalletBusinessStakeHolder);

			return businessStakeHolderModel;
		}
		catch (final HyperwalletException e) {
			log.error(String.format("Stakeholder not updated for clientId [%s].%n%s",
					businessStakeHolderModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e);
			mailNotificationUtil.sendPlainTextEmail("Issue detected when updating business stakeholder in Hyperwallet",
					String.format(ERROR_MESSAGE_PREFIX + "Business stakeholder not updated for clientId [%s]%n%s",
							businessStakeHolderModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
		}
		return null;
	}

	/**
	 * Checks whether the strategy must be executed based on the {@code source}
	 * @param source the source object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final BusinessStakeHolderModel source) {
		return Objects.nonNull(source.getToken());
	}

}
