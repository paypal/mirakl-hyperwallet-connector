package com.paypal.infrastructure.itemlinks.converters;

import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.MiraklItemLinkLocator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemLinksModelEntityConverter {

	@Mapping(target = "sourceId", source = "source.id")
	@Mapping(target = "sourceType", source = "source.type")
	@Mapping(target = "sourceSystem", source = "source.system")
	@Mapping(target = "targetId", source = "target.id")
	@Mapping(target = "targetType", source = "target.type")
	@Mapping(target = "targetSystem", source = "target.system")
	ItemLinkEntity from(MiraklItemLinkLocator source, HyperwalletItemLinkLocator target);

	@Mapping(target = "id", source = "targetId")
	@Mapping(target = "type", source = "targetType")
	HyperwalletItemLinkLocator hyperwalletLocatorFromLinkTarget(ItemLinkEntity itemLinkEntity);

}
