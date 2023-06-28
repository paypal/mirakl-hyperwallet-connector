package com.paypal.infrastructure.itemlinks.services;

import com.paypal.infrastructure.itemlinks.converters.ItemLinksModelEntityConverter;
import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.ItemLinkExternalSystem;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.repositories.ItemLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemLinksServiceImpl implements ItemLinksService {

	private final ItemLinkRepository itemLinkRepository;

	private final ItemLinksModelEntityConverter itemLinksModelEntityConverter;

	public ItemLinksServiceImpl(final ItemLinkRepository itemLinkRepository,
			final ItemLinksModelEntityConverter itemLinksModelEntityConverter) {
		this.itemLinkRepository = itemLinkRepository;
		this.itemLinksModelEntityConverter = itemLinksModelEntityConverter;
	}

	@Override
	public void createLinks(final MiraklItemLinkLocator miraklItemLocator,
			final Collection<HyperwalletItemLinkLocator> hyperwalletItemLocators) {
		// formatter:off
		final Collection<ItemLinkEntity> itemLinkEntities = hyperwalletItemLocators.stream()
				.map(hwItemLocator -> itemLinksModelEntityConverter.from(miraklItemLocator, hwItemLocator))
				.collect(Collectors.toSet());
		// formatter:on

		itemLinkRepository.saveAll(itemLinkEntities);
	}

	@Override
	public Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> findLinks(
			final Collection<MiraklItemLinkLocator> sourceItem, final Set<HyperwalletItemTypes> targetTypes) {
		// formatter:off
		return sourceItem.stream()
				.collect(Collectors.toMap(Function.identity(), source -> findLinks(source, targetTypes)));
		// formatter_on
	}

	@Override
	public Collection<HyperwalletItemLinkLocator> findLinks(final MiraklItemLinkLocator sourceItem,
			final Set<HyperwalletItemTypes> hyperwalletItemTypes) {
		// formatter:off
		final List<ItemLinkEntity> itemLinkEntityList = itemLinkRepository
				.findBySourceSystemAndSourceIdAndSourceTypeAndTargetSystemAndTargetTypeIn(
						sourceItem.getSystem().toString(), sourceItem.getId(), sourceItem.getType().toString(),
						ItemLinkExternalSystem.HYPERWALLET.toString(),
						hyperwalletItemTypes.stream().map(HyperwalletItemTypes::toString).collect(Collectors.toSet()));
		// formatter:on

		return itemLinkEntityList.stream().map(itemLinksModelEntityConverter::hyperwalletLocatorFromLinkTarget)
				.collect(Collectors.toSet());
	}

}
