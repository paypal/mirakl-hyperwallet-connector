package com.paypal.kyc.statussynchronization.services;

import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;

import java.util.Date;
import java.util.List;

public interface HyperwalletKycUserStatusExtractService {

	List<KYCUserStatusInfoModel> extractKycUserStatuses(Date from, Date to);

}
