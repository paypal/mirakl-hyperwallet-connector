package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder.VerificationStatus.REQUIRED;

/**
 * Implementation of {@link HyperwalletBusinessStakeholderExtractService}
 */
@Slf4j
@Service
@Getter
@Profile({ "!qa" })
public class HyperwalletBusinessStakeholderExtractServiceImpl implements HyperwalletBusinessStakeholderExtractService {

	private final HyperwalletSDKService hyperwalletSDKService;

	private final KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory;

	private final MailNotificationUtil kycMailNotificationUtil;

	protected HyperwalletBusinessStakeholderExtractServiceImpl(final HyperwalletSDKService hyperwalletSDKService,
			final KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory = kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getKYCRequiredVerificationBusinessStakeHolders(final String hyperwalletProgram,
			final String userToken) {
		final Hyperwallet hyperwallet = hyperwalletSDKService.getHyperwalletInstance(hyperwalletProgram);
		final HyperwalletList<HyperwalletBusinessStakeholder> businessStakeHolders = hyperwallet
				.listBusinessStakeholders(userToken);

		return Optional.ofNullable(businessStakeHolders).orElse(new HyperwalletList<>()).getData().stream()
				.filter(hyperwalletBusinessStakeholder -> REQUIRED
						.equals(hyperwalletBusinessStakeholder.getVerificationStatus()))
				.map(HyperwalletBusinessStakeholder::getToken).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<KYCDocumentBusinessStakeHolderInfoModel> pushBusinessStakeholderDocuments(
			final List<KYCDocumentBusinessStakeHolderInfoModel> kycBusinesStakeHolderInfoModels) {
		//@formatter:off
		if (CollectionUtils.isEmpty(kycBusinesStakeHolderInfoModels)) {
			return List.of();
		}

		final Map<String, List<KYCDocumentBusinessStakeHolderInfoModel>> businessStakeholdersGroupedByShops = kycBusinesStakeHolderInfoModels
				.stream()
				.collect(Collectors.groupingBy(KYCDocumentBusinessStakeHolderInfoModel::getClientUserId));

		printShopsToSkip(businessStakeholdersGroupedByShops);

		//@formatter:off
		final List<KYCDocumentBusinessStakeHolderInfoModel> businessStakeholderToBePushed = businessStakeholdersGroupedByShops.values()
				.stream()
				.filter(Predicate.not(this::hasAnyDocumentNotFilled))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		//@formatter:on

		//@formatter:on
		final Map<KYCDocumentBusinessStakeHolderInfoModel, List<HyperwalletVerificationDocument>> hyperwalletVerificationDocumentsToBePushed = businessStakeholderToBePushed
				.stream()
				.map(businessStakeHolderElement -> Pair.of(businessStakeHolderElement,
						kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory
								.execute(businessStakeHolderElement)))
				.collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
		//@formatter:off

		//@formatter:off
		return hyperwalletVerificationDocumentsToBePushed.entrySet()
				.stream()
				.filter(kycDocumentInfoModelListEntry -> ObjectUtils.isNotEmpty(kycDocumentInfoModelListEntry.getValue()))
				.map(this::callHyperwalletAPI)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@Override
	public List<KYCDocumentBusinessStakeHolderInfoModel> notifyAllDocumentsSentForBstk(
			final List<KYCDocumentBusinessStakeHolderInfoModel> documentsTriedToBeSent) {
		final Map<String, List<KYCDocumentBusinessStakeHolderInfoModel>> bstkGroupedByClientId = documentsTriedToBeSent
				.stream().collect(Collectors.groupingBy(KYCDocumentInfoModel::getUserToken));

		final Map<String, List<KYCDocumentBusinessStakeHolderInfoModel>> userWithBstkToBeNotified = bstkGroupedByClientId
				.entrySet().stream()
				.filter(entry -> entry.getValue().stream()
						.allMatch(KYCDocumentBusinessStakeHolderInfoModel::isSentToHyperwallet))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		//@formatter:off
		return userWithBstkToBeNotified.entrySet()
				.stream()
				.map(this::notifyBstk)
				.flatMap(Collection::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		//@formatter:on

	}

	protected List<KYCDocumentBusinessStakeHolderInfoModel> notifyBstk(
			final Map.Entry<String, List<KYCDocumentBusinessStakeHolderInfoModel>> entry) {
		final String token = entry.getKey();
		final HyperwalletUser user = new HyperwalletUser();
		user.setToken(token);
		user.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);

		try {
			final Optional<String> hyperwalletProgramOptional = getHyperwalletProgram(entry.getValue());
			if (hyperwalletProgramOptional.isPresent()) {
				final String hyperwalletProgram = hyperwalletProgramOptional.get();
				final Hyperwallet hyperwallet = hyperwalletSDKService.getHyperwalletInstance(hyperwalletProgram);
				final HyperwalletUser hyperwalletUser = hyperwallet.updateUser(user);
				log.info("Set as ready for review seller with id [{}]", hyperwalletUser.getClientUserId());
				return entry.getValue().stream()
						.map(bstk -> bstk.toBuilder().notifiedDocumentsReadyForReview(true).build())
						.collect(Collectors.toList());
			}
			else {
				log.error("Seller with shop Id [{}] has no Hyperwallet Program", getCliendId(entry.getValue()));
				return entry.getValue();
			}

		}
		catch (final HyperwalletException e) {
			//@formatter:off
			final String clientUserId = Optional.ofNullable(entry.getValue()).orElse(List.of())
					.stream()
					.map(KYCDocumentInfoModel::getClientUserId)
					.findAny()
					.orElse("undefined");
			//@formatter:on
			log.error("Error notifying to Hyperwallet that all documents were sent: [{}]",
					HyperwalletLoggingErrorsUtil.stringify(e));
			kycMailNotificationUtil.sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
					"There was an error notifying Hyperwallet all documents were sent for shop Id [%s], so Hyperwallet will not be notified about this new situation%n%s",
					clientUserId, HyperwalletLoggingErrorsUtil.stringify(e)));
			return entry.getValue();
		}
	}

	/**
	 * Manages the way to push KYC documents to another system
	 * @param entry cointains {@link KYCDocumentBusinessStakeHolderInfoModel} and its
	 * {@link List<HyperwalletVerificationDocument>} associated
	 * @return {@link KYCDocumentBusinessStakeHolderInfoModel} object when it has been
	 * processed succesfully. Null when it fails
	 */
	protected KYCDocumentBusinessStakeHolderInfoModel callHyperwalletAPI(
			final Map.Entry<KYCDocumentBusinessStakeHolderInfoModel, List<HyperwalletVerificationDocument>> entry) {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = entry.getKey();
		final String documentsToUpload = entry.getValue().stream()
				.map(hyperwalletVerificationDocument -> hyperwalletVerificationDocument.getUploadFiles().keySet())
				.flatMap(Collection::stream).collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR));
		try {
			final Hyperwallet hyperwallet = hyperwalletSDKService
					.getHyperwalletInstance(kycDocumentBusinessStakeHolderInfoModel.getHyperwalletProgram());
			hyperwallet.uploadStakeholderDocuments(kycDocumentBusinessStakeHolderInfoModel.getUserToken(),
					kycDocumentBusinessStakeHolderInfoModel.getToken(), entry.getValue());
			log.info("Documents [{}] uploaded for shop with id [{}] and business stakeholder number {}",
					documentsToUpload, kycDocumentBusinessStakeHolderInfoModel.getClientUserId(),
					kycDocumentBusinessStakeHolderInfoModel.getBusinessStakeholderMiraklNumber());
			return kycDocumentBusinessStakeHolderInfoModel.toBuilder().sentToHyperwallet(true).build();
		}
		catch (final HyperwalletException e) {
			log.error("Error uploading document to hyperwallet: [{}]", HyperwalletLoggingErrorsUtil.stringify(e));
			kycMailNotificationUtil.sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
					String.format(
							"Something went wrong pushing documents to Hyperwallet for shop Id [%s] and business stakeholder number [%s]%n%s",
							kycDocumentBusinessStakeHolderInfoModel.getClientUserId(),
							kycDocumentBusinessStakeHolderInfoModel.getBusinessStakeholderMiraklNumber(),
							HyperwalletLoggingErrorsUtil.stringify(e)));
			return kycDocumentBusinessStakeHolderInfoModel;
		}
	}

	private void printShopsToSkip(
			final Map<String, List<KYCDocumentBusinessStakeHolderInfoModel>> kycDocumentInfoModelCollection) {
		//@formatter:off
		final List<String> shopsToSkip = Optional.ofNullable(kycDocumentInfoModelCollection).orElse(Map.of())
				.entrySet()
				.stream()
				.filter(entry -> hasAnyDocumentNotFilled(entry.getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(shopsToSkip)) {
			log.warn("Mandatory documents missing for shop with id [{}] ", String.join(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR, shopsToSkip));
		}
		//@formatter:on
	}

	private boolean hasAnyDocumentNotFilled(
			final List<KYCDocumentBusinessStakeHolderInfoModel> businessStakeHolderInfoModelList) {
		return Stream.ofNullable(businessStakeHolderInfoModelList).flatMap(Collection::stream)
				.anyMatch(Predicate.not(KYCDocumentBusinessStakeHolderInfoModel::areDocumentsFilled));
	}

	private Optional<String> getHyperwalletProgram(
			final List<KYCDocumentBusinessStakeHolderInfoModel> kYCDocumentBusinessStakeHolderInfoModelList) {
		//@formatter:off
		return Optional.ofNullable(kYCDocumentBusinessStakeHolderInfoModelList).orElse(List.of())
				.stream()
				.filter(Objects::nonNull)
				.map(KYCDocumentInfoModel::getHyperwalletProgram)
				.filter(StringUtils::isNotEmpty)
				.findAny();
		//@formatter:off
	}

	private String getCliendId(
			final List<KYCDocumentBusinessStakeHolderInfoModel> kYCDocumentBusinessStakeHolderInfoModelList) {
		//@formatter:off
		return Optional.ofNullable(kYCDocumentBusinessStakeHolderInfoModelList).orElse(List.of())
				.stream()
				.filter(Objects::nonNull)
				.map(KYCDocumentInfoModel::getClientUserId)
				.findAny()
				.orElse(null);
		//@formatter:on
	}

}
