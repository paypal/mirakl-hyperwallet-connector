package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import com.paypal.sellers.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class HyperWalletCreateBusinessStakeHolderServiceStrategy
		implements Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel> {

	private final Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverter;

	private final HyperwalletSDKService hyperwalletSDKService;

	private final MailNotificationUtil mailNotificationUtil;

	private final MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractService;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public HyperWalletCreateBusinessStakeHolderServiceStrategy(
			final Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverter,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil,
			final MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractService) {
		this.businessStakeHolderModelHyperwalletBusinessStakeholderConverter = businessStakeHolderModelHyperwalletBusinessStakeholderConverter;
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.mailNotificationUtil = mailNotificationUtil;
		this.miraklBusinessStakeholderExtractService = miraklBusinessStakeholderExtractService;
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

			final Hyperwallet hyperwallet = hyperwalletSDKService
					.getHyperwalletInstanceByHyperwalletProgram(businessStakeHolderModel.getHyperwalletProgram());

			final HyperwalletBusinessStakeholder hyperWalletBusinessStakeHolderResponse = hyperwallet
					.createBusinessStakeholder(businessStakeHolderModel.getUserToken(), hyperWalletBusinessStakeHolder);

			final BusinessStakeHolderModel createdBusinessStakeHolderModel = businessStakeHolderModel.toBuilder()
					.token(hyperWalletBusinessStakeHolderResponse.getToken()).justCreated(true).build();

			miraklBusinessStakeholderExtractService.updateBusinessStakeholderToken(
					createdBusinessStakeHolderModel.getClientUserId(), List.of(createdBusinessStakeHolderModel));

			return createdBusinessStakeHolderModel;
		}
		catch (final HyperwalletException e) {
			log.error(String.format("Stakeholder not created for clientId [%s].%n%s",
					businessStakeHolderModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e);
			mailNotificationUtil.sendPlainTextEmail("Issue detected when creating business stakeholder in Hyperwallet",
					String.format(ERROR_MESSAGE_PREFIX + "Business stakeholder not created for clientId [%s]%n%s",
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
		return Objects.isNull(source.getToken());
	}

}
