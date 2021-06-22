package com.paypal.sellers.bankaccountextract.service.impl;

import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.repository.FailedBankAccountInformationRepository;
import com.paypal.sellers.service.FailedEntityInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FailedEntityInformationService}
 */
@Service
@Slf4j
public class FailedBankAccountInformationServiceImpl
		implements FailedEntityInformationService<FailedBankAccountInformation> {

	private final FailedBankAccountInformationRepository failedBankAccountInformationRepository;

	public FailedBankAccountInformationServiceImpl(
			final FailedBankAccountInformationRepository failedBankAccountInformationRepository) {
		this.failedBankAccountInformationRepository = failedBankAccountInformationRepository;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void saveAll(final List<String> shopIds) {
		if (Objects.nonNull(shopIds)) {
			final List<FailedBankAccountInformation> failedShopEntities = shopIds.stream()
					.map(shopId -> FailedBankAccountInformation.builder().shopId(shopId).build())
					.collect(Collectors.toList());
			failedBankAccountInformationRepository.saveAll(failedShopEntities);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(final String shopId) {
		saveAll(List.of(shopId));
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public List<FailedBankAccountInformation> getAll() {
		return failedBankAccountInformationRepository.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteByShopId(final String shopId) {
		if (failedBankAccountInformationRepository.deleteByShopId(shopId) > 0) {
			log.info("Retry Process: ShopId {} information was sucessfully created/updated after retrying.", shopId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FailedBankAccountInformation> findByShopId(final String shopId) {
		return failedBankAccountInformationRepository.findByShopId(shopId);
	}

}
