package com.paypal.sellers.sellersextract.service.impl;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderConstants;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paypal.sellers.sellersextract.service.impl.BusinessStakeholderExtractServiceImpl.ERROR_MESSAGE_PREFIX;

/**
 * Class that updates the token extracted from hyperwallet
 */
@Slf4j
@Service
public class MiraklBusinessStakeholderExtractServiceImpl implements MiraklBusinessStakeholderExtractService {

	private static final String EMAIL_SUBJECT_MESSAGE = "Issue detected getting shop information in Mirakl";

	private static final String HYPHEN = "-";

	private final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	private final MailNotificationUtil sellerMailNotificationUtil;

	public MiraklBusinessStakeholderExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
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
		log.debug("Update shop request [{}]", ToStringBuilder.reflectionToString(request));
		try {
			final MiraklUpdatedShops miraklUpdatedShops = miraklOperatorClient.updateShops(request);
			Optional.ofNullable(miraklUpdatedShops).ifPresent(
					response -> log.debug("Update shop response [{}]", ToStringBuilder.reflectionToString(response)));
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting information of shop [{}]", clientUserId);
			sellerMailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					String.format(ERROR_MESSAGE_PREFIX + "Something went wrong getting information of shop [%s]%n%s",
							clientUserId, MiraklLoggingErrorsUtil.stringify(ex)));
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
