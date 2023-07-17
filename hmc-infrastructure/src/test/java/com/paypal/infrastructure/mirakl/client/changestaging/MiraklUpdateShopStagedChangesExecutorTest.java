package com.paypal.infrastructure.mirakl.client.changestaging;

import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesExecutorInfo;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.testsupport.TestDateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MiraklUpdateShopStagedChangesExecutorTest {

	@InjectMocks
	private MiraklUpdateShopStagedChangesExecutor testObj;

	@Mock
	private MiraklClient miraklClientMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> argumentCaptor;

	@Test
	void execute_shouldUpdateTheShops_whenExecutedWithAListOfChanges_compactingChangesFromShop() {
		// given
		//@formatter:off
		final List<StagedChange> stagedChanges = new ArrayList<>(List.of(
				stagedChange(1, 1, x -> {}),
				stagedChange(2, 2, x -> {}),
				stagedChange(3, 1, x -> {}),
				stagedChange(2, 3, x -> {})
		));
		//@formatter:on

		// when
		testObj.execute(stagedChanges);

		// then
		verify(miraklClientMock).updateShops(argumentCaptor.capture());
		final MiraklUpdateShopsRequest miraklUpdateShopsRequestCaptured = argumentCaptor.getValue();
		final List<MiraklUpdateShop> compactedShops = miraklUpdateShopsRequestCaptured.getShops();

		assertThat(compactedShops).hasSize(3);
		assertThat(compactedShops.get(0).getShopId()).isEqualTo(1L);
		assertThat(compactedShops.get(1).getShopId()).isEqualTo(2L);
		assertThat(compactedShops.get(2).getShopId()).isEqualTo(3L);
	}

	@Test
	void execute_shouldCompactAndAggregateFieldsFromDifferentChangesOfTheSameShop() {
		// given
		//@formatter:off
		final List<StagedChange> stagedChanges = new ArrayList<>(List.of(
				stagedChange(1, 1, x -> x.setDescription("Description")),
				stagedChange(1, 2, x -> x.setName("Name"))
		));
		//@formatter:on

		// when
		testObj.execute(stagedChanges);

		// then
		verify(miraklClientMock).updateShops(argumentCaptor.capture());
		final MiraklUpdateShopsRequest miraklUpdateShopsRequestCaptured = argumentCaptor.getValue();
		final List<MiraklUpdateShop> compactedShops = miraklUpdateShopsRequestCaptured.getShops();

		assertThat(compactedShops).hasSize(1);
		assertThat(compactedShops.get(0).getShopId()).isEqualTo(1L);
		assertThat(compactedShops.get(0).getDescription()).isEqualTo("Description");
		assertThat(compactedShops.get(0).getName()).isEqualTo("Name");
	}

	@Test
	void execute_shouldCompactAndHandlePatchesOfBooleanValues() {
		// given
		//@formatter:off
		final List<StagedChange> stagedChanges = new ArrayList<>(List.of(
				stagedChange(1, 1, x -> x.setProfessional(false)),
				stagedChange(2, 1, x -> x.setProfessional(true)),
				stagedChange(3, 1, x -> {})
		));
		//@formatter:on

		// when
		testObj.execute(stagedChanges);

		// then
		verify(miraklClientMock).updateShops(argumentCaptor.capture());
		final MiraklUpdateShopsRequest miraklUpdateShopsRequestCaptured = argumentCaptor.getValue();
		final List<MiraklUpdateShop> compactedShops = miraklUpdateShopsRequestCaptured.getShops();

		assertThat(compactedShops.get(0).isProfessional()).isFalse();
		assertThat(compactedShops.get(1).isProfessional()).isTrue();
		assertThat(ReflectionTestUtils.getField(compactedShops.get(2), "professional")).isNull();
	}

	@Test
	void execute_shouldCompactAndKeepTheValueOfLaterDateWhenBothStoresHaveAPropertyFilled_whenExecuted() {
		// given
		//@formatter:off
		final List<StagedChange> stagedChanges = new ArrayList<>(List.of(
				stagedChange(1, 2, x -> x.setDescription("Old description")),
				stagedChange(1, 1, x -> x.setDescription("New description"))
		));
		//@formatter:on

		// when
		testObj.execute(stagedChanges);

		// then
		verify(miraklClientMock).updateShops(argumentCaptor.capture());
		final MiraklUpdateShopsRequest miraklUpdateShopsRequestCaptured = argumentCaptor.getValue();
		final List<MiraklUpdateShop> compactedShops = miraklUpdateShopsRequestCaptured.getShops();
		final MiraklUpdateShop firstMiraklUpdateShop = compactedShops.get(0);
		assertThat(firstMiraklUpdateShop.getDescription()).isEqualTo("New description");
	}

	@Test
	void getExecutorInfo_shouldCreateAndReturnStagedChangesExecutorInfo_whenExecuted() {
		// when
		final StagedChangesExecutorInfo stagedChangesExecutorInfo = testObj.getExecutorInfo();

		// then
		assertThat(stagedChangesExecutorInfo).isNotNull();
		assertThat(stagedChangesExecutorInfo.getOperation()).isEqualTo(ChangeOperation.UPDATE);
		assertThat(stagedChangesExecutorInfo.getTarget()).isEqualTo(ChangeTarget.MIRAKL);
		assertThat(stagedChangesExecutorInfo.getType()).isEqualTo(MiraklUpdateShop.class);
	}

	private StagedChange stagedChange(final long shopId, final long daysOffset,
			final Consumer<MiraklUpdateShop> updatePayload) {
		final StagedChange stagedChange1 = new StagedChange();
		stagedChange1.setType(MiraklUpdateShop.class);
		stagedChange1.setId("%s-%s".formatted(shopId, UUID.randomUUID().toString()));
		stagedChange1.setTarget(ChangeTarget.MIRAKL);
		stagedChange1.setOperation(ChangeOperation.UPDATE);
		stagedChange1.setCreationDate(TestDateUtil.currentDateMinusDays(daysOffset));

		final MiraklUpdateShop payload = new MiraklUpdateShop();
		payload.setShopId(shopId);
		stagedChange1.setPayload(payload);

		updatePayload.accept(payload);

		return stagedChange1;
	}

}
