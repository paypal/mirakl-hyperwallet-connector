package com.paypal.infrastructure.itemlinks.services;

import com.paypal.infrastructure.itemlinks.converters.ItemLinksModelEntityConverter;
import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.infrastructure.itemlinks.model.ItemLinkExternalSystem;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import com.paypal.infrastructure.itemlinks.repository.ItemLinkRepository;
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

	public ItemLinksServiceImpl(ItemLinkRepository itemLinkRepository,
			ItemLinksModelEntityConverter itemLinksModelEntityConverter) {
		this.itemLinkRepository = itemLinkRepository;
		this.itemLinksModelEntityConverter = itemLinksModelEntityConverter;
	}

	@Override
	public void createLinks(MiraklItemLinkLocator miraklItemLocator,
			Collection<HyperwalletItemLinkLocator> hyperwalletItemLocators) {
		// formatter:off
		Collection<ItemLinkEntity> itemLinkEntities = hyperwalletItemLocators.stream()
				.map(hwItemLocator -> itemLinksModelEntityConverter.from(miraklItemLocator, hwItemLocator))
				.collect(Collectors.toSet());
		// formatter:on

		itemLinkRepository.saveAll(itemLinkEntities);
	}

	@Override
	public Map<MiraklItemLinkLocator, Collection<HyperwalletItemLinkLocator>> findLinks(
			Collection<MiraklItemLinkLocator> sourceItem, Set<HyperwalletItemTypes> targetTypes) {
		// formatter:off
		return sourceItem.stream()
				.collect(Collectors.toMap(Function.identity(), source -> findLinks(source, targetTypes)));
		// formatter_on
	}

	@Override
	public Collection<HyperwalletItemLinkLocator> findLinks(MiraklItemLinkLocator sourceItem,
			Set<HyperwalletItemTypes> hyperwalletItemTypes) {
		// formatter:off
		List<ItemLinkEntity> itemLinkEntityList = itemLinkRepository
				.findBySourceSystemAndSourceIdAndSourceTypeAndTargetSystemAndTargetTypeIn(
						sourceItem.getSystem().toString(), sourceItem.getId(), sourceItem.getType().toString(),
						ItemLinkExternalSystem.HYPERWALLET.toString(),
						hyperwalletItemTypes.stream().map(HyperwalletItemTypes::toString).collect(Collectors.toSet()));
		// formatter:on

		return itemLinkEntityList.stream().map(itemLinksModelEntityConverter::hyperwalletLocatorFromLinkTarget)
				.collect(Collectors.toSet());
	}

}
