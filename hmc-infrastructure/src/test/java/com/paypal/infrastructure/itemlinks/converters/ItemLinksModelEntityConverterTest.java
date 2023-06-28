package com.paypal.infrastructure.itemlinks.converters;

import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import com.paypal.infrastructure.itemlinks.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemLinksModelEntityConverterTest {

	@Spy
	private final ItemLinksModelEntityConverter testObj = Mappers.getMapper(ItemLinksModelEntityConverter.class);

	@Test
	void hyperwalletLocatorFromLinkTarget_shouldCreateHyperwalletItemLocator() {
		//@formatter:off
		final ItemLinkEntity itemLinkEntity = ItemLinkEntity.builder()
				.sourceSystem(ItemLinkExternalSystem.MIRAKL.toString())
				.sourceType(MiraklItemTypes.SHOP.toString())
				.sourceId("M1")
				.targetSystem(ItemLinkExternalSystem.HYPERWALLET.toString())
				.targetType(HyperwalletItemTypes.BANK_ACCOUNT.toString())
				.targetId("H1")
				.build();
		//@formatter:on

		final HyperwalletItemLinkLocator result = testObj.hyperwalletLocatorFromLinkTarget(itemLinkEntity);

		assertThat(result.getId()).isEqualTo("H1");
		assertThat(result.getType()).isEqualTo(HyperwalletItemTypes.BANK_ACCOUNT);
	}

	@Test
	void from_shouldCreateEntityFromLocators() {
		final HyperwalletItemLinkLocator hyperwalletItemLocator = new HyperwalletItemLinkLocator("H1",
				HyperwalletItemTypes.PAYMENT);
		final MiraklItemLinkLocator miraklItemLocator = new MiraklItemLinkLocator("M1", MiraklItemTypes.SHOP);

		final ItemLinkEntity itemLinkEntity = testObj.from(miraklItemLocator, hyperwalletItemLocator);

		assertThat(itemLinkEntity.getSourceId()).isEqualTo("M1");
		assertThat(itemLinkEntity.getSourceType()).isEqualTo(MiraklItemTypes.SHOP.toString());
		assertThat(itemLinkEntity.getSourceSystem()).isEqualTo(ItemLinkExternalSystem.MIRAKL.toString());
		assertThat(itemLinkEntity.getTargetId()).isEqualTo("H1");
		assertThat(itemLinkEntity.getTargetType()).isEqualTo(HyperwalletItemTypes.PAYMENT.toString());
		assertThat(itemLinkEntity.getTargetSystem()).isEqualTo(ItemLinkExternalSystem.HYPERWALLET.toString());
	}

}
