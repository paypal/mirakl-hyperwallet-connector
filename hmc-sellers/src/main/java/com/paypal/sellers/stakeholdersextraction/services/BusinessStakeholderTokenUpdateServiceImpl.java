package com.paypal.sellers.stakeholdersextraction.services;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderConstants;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.paypal.sellers.stakeholdersextraction.services.BusinessStakeholderExtractServiceImpl.ERROR_MESSAGE_PREFIX;

/**
 * Class that updates the token extracted from hyperwallet
 */
@Slf4j
@Service
public class BusinessStakeholderTokenUpdateServiceImpl implements BusinessStakeholderTokenUpdateService {

	private static final String EMAIL_SUBJECT_MESSAGE = "Issue detected getting shop information in Mirakl";

	private static final String HYPHEN = "-";

	private final MiraklClient miraklOperatorClient;

	private final MailNotificationUtil sellerMailNotificationUtil;

	public BusinessStakeholderTokenUpdateServiceImpl(final MiraklClient miraklOperatorClient,
			final MailNotificationUtil sellerMailNotificationUtil) {
		this.miraklOperatorClient = miraklOperatorClient;
		this.sellerMailNotificationUtil = sellerMailNotificationUtil;
	}

	@Override
	public void updateBusinessStakeholderToken(final String clientUserId,
			final List<BusinessStakeHolderModel> businessStakeHolderModels) {
		if (CollectionUtils.isEmpty(businessStakeHolderModels)) {
			log.info("No data for business stakeholders on store [{}]", clientUserId);
			return;
		}

		final MiraklUpdateShop miraklUpdateShop = createMiraklUpdateFieldRequestForStakeholders(clientUserId,
				businessStakeHolderModels);
		final MiraklUpdateShopsRequest request = new MiraklUpdateShopsRequest(List.of(miraklUpdateShop));
		try {
			miraklOperatorClient.updateShops(request);
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting information of shop [{}]", clientUserId);
			sellerMailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					(ERROR_MESSAGE_PREFIX + "Something went wrong getting information of shop [%s]%n%s")
							.formatted(clientUserId, MiraklLoggingErrorsUtil.stringify(ex)));
		}
	}

	private MiraklUpdateShop createMiraklUpdateFieldRequestForStakeholders(final String clientUserId,
			final List<BusinessStakeHolderModel> businessStakeHolderModels) {
		final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
		miraklUpdateShop.setShopId(Long.valueOf(clientUserId));

		final List<String> stakeholderTokens = new ArrayList<>();
		final List<MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue> stakeholdersTokenFields = businessStakeHolderModels
				.stream().map(businessStakeHolderModel -> {
					stakeholderTokens.add(businessStakeHolderModel.getToken());
					return new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
							BusinessStakeHolderConstants.TOKEN + HYPHEN + businessStakeHolderModel.getStkId(),
							businessStakeHolderModel.getToken());
				}).collect(Collectors.toList());

		log.info("Storing business stakeholder tokens [{}] for shop [{}]", String.join(",", stakeholderTokens),
				clientUserId);
		miraklUpdateShop.setAdditionalFieldValues(Collections.unmodifiableList(stakeholdersTokenFields));

		return miraklUpdateShop;
	}

}
