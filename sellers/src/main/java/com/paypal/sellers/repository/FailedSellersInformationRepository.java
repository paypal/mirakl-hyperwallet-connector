package com.paypal.sellers.repository;

import com.paypal.sellers.entity.FailedSellersInformation;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link FailedSellersInformationRepository}
 */
@Repository
public interface FailedSellersInformationRepository extends FailedShopInformationRepository<FailedSellersInformation> {

}
