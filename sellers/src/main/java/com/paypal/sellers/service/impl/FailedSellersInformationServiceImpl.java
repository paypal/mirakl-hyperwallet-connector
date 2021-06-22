package com.paypal.sellers.service.impl;

import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.repository.FailedSellersInformationRepository;
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
public class FailedSellersInformationServiceImpl implements FailedEntityInformationService<FailedSellersInformation> {

	private final FailedSellersInformationRepository failedSellersInformationRepository;

	public FailedSellersInformationServiceImpl(
			final FailedSellersInformationRepository failedSellersInformationRepository) {
		this.failedSellersInformationRepository = failedSellersInformationRepository;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void saveAll(final List<String> shopIds) {
		if (Objects.nonNull(shopIds)) {
			final List<FailedSellersInformation> failedShopEntities = shopIds.stream()
					.map(shopId -> FailedSellersInformation.builder().shopId(shopId).build())
					.collect(Collectors.toList());
			failedSellersInformationRepository.saveAll(failedShopEntities);
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
	public List<FailedSellersInformation> getAll() {
		return failedSellersInformationRepository.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteByShopId(final String shopId) {
		failedSellersInformationRepository.deleteByShopId(shopId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FailedSellersInformation> findByShopId(final String shopId) {
		return failedSellersInformationRepository.findByShopId(shopId);
	}

}
