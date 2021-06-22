package com.paypal.sellers.repository;

import com.paypal.sellers.entity.AbstractFailedShopInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface FailedShopInformationRepository<T extends AbstractFailedShopInformation>
		extends JpaRepository<T, Long> {

	int deleteByShopId(String shopId);

	List<T> findByShopId(String shopId);

}
