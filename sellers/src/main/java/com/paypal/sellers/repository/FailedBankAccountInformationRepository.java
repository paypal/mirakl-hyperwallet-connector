package com.paypal.sellers.repository;

import com.paypal.sellers.entity.FailedBankAccountInformation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link FailedBankAccountInformationRepository}
 */
@Repository
@Transactional
public interface FailedBankAccountInformationRepository
		extends FailedShopInformationRepository<FailedBankAccountInformation> {

}
