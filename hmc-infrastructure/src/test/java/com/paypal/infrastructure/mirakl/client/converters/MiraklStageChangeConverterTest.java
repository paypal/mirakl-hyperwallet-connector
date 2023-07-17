package com.paypal.infrastructure.mirakl.client.converters;

import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklStageChangeConverterTest {

	@Spy
	private final MiraklStageChangeConverter testObj = Mappers.getMapper(MiraklStageChangeConverter.class);

	@Test
	void from_shouldCreateChangeFromMiraklUpdateShop() {
		// given
		final MiraklUpdateShop sourceUpdateShop = generateTestMiraklUpdateShop(1L);

		// when
		final Change targetChange = testObj.from(sourceUpdateShop);

		// then
		assertIndividualChange(sourceUpdateShop, targetChange);
	}

	@Test
	void multipleFrom_shouldCreateAllChangesFromMiraklUpdateShops() {
		// given
		final List<MiraklUpdateShop> sourceUpdateShopList = List.of(generateTestMiraklUpdateShop(1L),
				generateTestMiraklUpdateShop(2L), generateTestMiraklUpdateShop(3L));

		// when
		final List<Change> targetChanges = testObj.from(sourceUpdateShopList);

		// then
		IntStream.range(0, sourceUpdateShopList.size())
				.forEach(index -> assertIndividualChange(sourceUpdateShopList.get(index), targetChanges.get(index)));
	}

	private static void assertIndividualChange(final MiraklUpdateShop sourceUpdateShop, final Change targetChange) {
		assertThat(targetChange.getType()).isEqualTo(MiraklUpdateShop.class);
		assertThat(targetChange.getPayload()).isEqualTo(sourceUpdateShop);
		assertThat(targetChange.getOperation()).isEqualTo(ChangeOperation.UPDATE);
		assertThat(targetChange.getTarget()).isEqualTo(ChangeTarget.MIRAKL);
	}

	@NotNull
	private static MiraklUpdateShop generateTestMiraklUpdateShop(final long shopId) {
		final MiraklUpdateShop sourceUpdateShop = new MiraklUpdateShop();
		sourceUpdateShop.setShopId(shopId);
		sourceUpdateShop.setName("testShop");
		return sourceUpdateShop;
	}

}
