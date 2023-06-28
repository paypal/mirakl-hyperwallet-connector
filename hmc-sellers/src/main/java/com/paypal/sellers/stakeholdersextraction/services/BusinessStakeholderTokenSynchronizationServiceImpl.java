package com.paypal.sellers.stakeholdersextraction.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.services.TokenSynchronizationService;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Class that implements the {@link TokenSynchronizationService} interface for the
 * synchronization of tokens for business stakeholders
 */
@Slf4j
@Service("businessStakeholdersTokenSynchronizationService")
public class BusinessStakeholderTokenSynchronizationServiceImpl
		implements TokenSynchronizationService<BusinessStakeHolderModel> {

	@Value("${hmc.jobs.settings.stk-mandatory-email}")
	private boolean isStkEmailMandatory;

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	private final BusinessStakeholderTokenUpdateService businessStakeholderTokenUpdateService;

	private final MailNotificationUtil mailNotificationUtil;

	public BusinessStakeholderTokenSynchronizationServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final BusinessStakeholderTokenUpdateService businessStakeholderTokenUpdateService,
			final MailNotificationUtil mailNotificationUtil) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.businessStakeholderTokenUpdateService = businessStakeholderTokenUpdateService;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * Ensures the Business stakeholder's token between Hyperwallet and Mirakl is
	 * synchronized.
	 * @param businessStakeHolderModel to be synchronized.
	 * @return the businessStakeHolderModel with the token synchronized.
	 */
	@Override
	public BusinessStakeHolderModel synchronizeToken(final BusinessStakeHolderModel businessStakeHolderModel) {

		checkBusinessStakeHolderEmail(businessStakeHolderModel);

		if (StringUtils.isNotBlank(businessStakeHolderModel.getToken())) {

			log.debug(
					"Hyperwallet token already exists for business stakeholder [{}] for shop [{}], synchronization not needed",
					businessStakeHolderModel.getToken(), businessStakeHolderModel.getClientUserId());

			return businessStakeHolderModel;
		}

		final Optional<HyperwalletBusinessStakeholder> hyperwalletBusinessStakeholder = getHwBusinessStakeHolder(
				businessStakeHolderModel);

		if (hyperwalletBusinessStakeholder.isPresent()) {

			log.debug("Hyperwallet business stakeholder with email [{}] for shop [{}] found",
					businessStakeHolderModel.getEmail(), businessStakeHolderModel.getClientUserId());

			final BusinessStakeHolderModel synchronizedBusinessStakeHolderModel = updateBusinessStakeHolderWithHyperwalletToken(
					businessStakeHolderModel, hyperwalletBusinessStakeholder.get());

			updateTokenInMirakl(synchronizedBusinessStakeHolderModel);

			return synchronizedBusinessStakeHolderModel;
		}
		else {

			log.debug("Hyperwallet business stakeholder [{}] for shop [{}] not found",
					businessStakeHolderModel.getStkId(), businessStakeHolderModel.getClientUserId());

			return businessStakeHolderModel;
		}
	}

	private void checkBusinessStakeHolderEmail(final BusinessStakeHolderModel businessStakeHolderModel) {

		if (isStkEmailMandatory() && StringUtils.isBlank(businessStakeHolderModel.getEmail())
				&& StringUtils.isBlank(businessStakeHolderModel.getToken())) {

			mailNotificationUtil.sendPlainTextEmail(
					String.format("Validation error occurred when processing a stakeholder for seller {%s}",
							businessStakeHolderModel.getClientUserId()),
					String.format(
							"There was an error processing the {%s} seller and the operation could not be completed. Email address for the stakeholder {%d} must be filled.\n"
									+ "Please check the logs for further information.",
							businessStakeHolderModel.getClientUserId(), businessStakeHolderModel.getStkId()));

			throw new HMCException(
					String.format("Business stakeholder without email and Hyperwallet token defined for shop [%s]",
							businessStakeHolderModel.getClientUserId()));
		}
	}

	private void updateTokenInMirakl(final BusinessStakeHolderModel synchronizedBusinessStakeHolderModel) {

		try {

			businessStakeholderTokenUpdateService.updateBusinessStakeholderToken(
					synchronizedBusinessStakeHolderModel.getClientUserId(),
					List.of(synchronizedBusinessStakeHolderModel));
		}
		catch (final MiraklException e) {

			log.error(String.format("Error while updating Mirakl business stakeholder [%s] for shop [%s]",
					synchronizedBusinessStakeHolderModel.getToken(),
					synchronizedBusinessStakeHolderModel.getClientUserId()), e);
			throw new HMCMiraklAPIException(e);
		}
	}

	@SuppressWarnings("java:S3516") // Sonar false positive
	private Optional<HyperwalletBusinessStakeholder> getHwBusinessStakeHolder(
			final BusinessStakeHolderModel businessStakeHolderModel) {

		if (StringUtils.isBlank(businessStakeHolderModel.getEmail())) {

			return Optional.empty();
		}
		else {

			final HyperwalletList<HyperwalletBusinessStakeholder> hwBusinessStakeHolders = getHwBusinessStakeHoldersByUserToken(
					businessStakeHolderModel);

			//@formatter:off
			return Stream.ofNullable(hwBusinessStakeHolders.getData())
					.flatMap(Collection::stream)
					.filter(hwstk -> businessStakeHolderModel.getEmail().equals(hwstk.getEmail()))
					.findFirst();
			//@formatter:on
		}
	}

	private HyperwalletList<HyperwalletBusinessStakeholder> getHwBusinessStakeHoldersByUserToken(
			final BusinessStakeHolderModel businessStakeHolderModel) {

		final Hyperwallet hyperwalletSDK = userHyperwalletSDKService
				.getHyperwalletInstanceByHyperwalletProgram(businessStakeHolderModel.getHyperwalletProgram());

		try {

			return hyperwalletSDK.listBusinessStakeholders(businessStakeHolderModel.getUserToken());
		}
		catch (final HyperwalletException e) {
			log.error(String.format("Error while getting Hyperwallet business stakeholders for shop [%s].%n%s",
					businessStakeHolderModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e);

			throw new HMCHyperwalletAPIException(e);
		}
	}

	private BusinessStakeHolderModel updateBusinessStakeHolderWithHyperwalletToken(
			final BusinessStakeHolderModel businessStakeHolderModel,
			final HyperwalletBusinessStakeholder hyperwalletBusinessStakeholder) {

		return businessStakeHolderModel.toBuilder().token(hyperwalletBusinessStakeholder.getToken()).build();
	}

	protected boolean isStkEmailMandatory() {
		return isStkEmailMandatory;
	}

}
