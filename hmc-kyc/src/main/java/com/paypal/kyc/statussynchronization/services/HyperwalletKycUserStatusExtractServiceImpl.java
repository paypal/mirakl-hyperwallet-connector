package com.paypal.kyc.statussynchronization.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletUsersListPaginationOptions;
import com.paypal.infrastructure.hyperwallet.services.HyperwalletPaginationSupport;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HyperwalletKycUserStatusExtractServiceImpl implements HyperwalletKycUserStatusExtractService {

	private final Hyperwallet hyperwallet;

	private final Converter<HyperwalletUser, KYCUserStatusInfoModel> kycUserStatusInfoModelConverter;

	public HyperwalletKycUserStatusExtractServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final Converter<HyperwalletUser, KYCUserStatusInfoModel> kycUserStatusInfoModelConverter) {
		this.hyperwallet = userHyperwalletSDKService.getHyperwalletInstance();
		this.kycUserStatusInfoModelConverter = kycUserStatusInfoModelConverter;
	}

	@Override
	public List<KYCUserStatusInfoModel> extractKycUserStatuses(final Date from, final Date to) {
		final HyperwalletUsersListPaginationOptions options = new HyperwalletUsersListPaginationOptions();
		options.setCreatedAfter(from);
		options.setCreatedBefore(to);
		final HyperwalletPaginationSupport hyperwalletPaginationSupport = new HyperwalletPaginationSupport(hyperwallet);
		final List<HyperwalletUser> hyperwalletUsers = hyperwalletPaginationSupport
				.get(() -> hyperwallet.listUsers(options));

		return hyperwalletUsers.stream().map(kycUserStatusInfoModelConverter::convert).collect(Collectors.toList());
	}

}
