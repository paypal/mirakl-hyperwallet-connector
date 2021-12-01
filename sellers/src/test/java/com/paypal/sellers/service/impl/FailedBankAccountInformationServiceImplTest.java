package com.paypal.sellers.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.sellers.bankaccountextract.service.impl.FailedBankAccountInformationServiceImpl;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.repository.FailedBankAccountInformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FailedBankAccountInformationServiceImplTest {

	private static final String SHOP_1 = "shop1";

	private static final String SHOP_2 = "shop2";

	@InjectMocks
	private FailedBankAccountInformationServiceImpl testObj;

	@Mock
	private FailedBankAccountInformationRepository failedBankAccountInformationRepositoryMock;

	@Mock
	private FailedBankAccountInformation failedBankAccountInformationOneMock, failedBankAccountInformationTwoMock;

	@Captor
	private ArgumentCaptor<List<FailedBankAccountInformation>> failedBankAccountInformationArgumentCaptor;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(FailedBankAccountInformationServiceImpl.class);

	@Test
	void saveAll_shouldSaveAllShopsIntoDatabase_whenShopsAreReceived() {
		testObj.saveAll(List.of(SHOP_1, SHOP_2));

		verify(failedBankAccountInformationRepositoryMock)
				.saveAll(failedBankAccountInformationArgumentCaptor.capture());
		final List<FailedBankAccountInformation> shopIds = failedBankAccountInformationArgumentCaptor.getValue();
		assertThat(shopIds).hasSize(2);
	}

	@Test
	void save_shouldSaveShopIdIntoDatabase_whenShopIdIsReceived() {
		testObj.save(SHOP_1);

		verify(failedBankAccountInformationRepositoryMock)
				.saveAll(failedBankAccountInformationArgumentCaptor.capture());
		final List<FailedBankAccountInformation> shopIds = failedBankAccountInformationArgumentCaptor.getValue();
		assertThat(shopIds).hasSize(1);
	}

	@Test
	void saveAll_shouldDoNothing_whenNullIsReceivedAsParameter() {
		testObj.saveAll(null);

		verifyNoInteractions(failedBankAccountInformationRepositoryMock);
	}

	@Test
	void getAll_shouldGetAllFailedSellersInformationEntity() {
		when(failedBankAccountInformationRepositoryMock.findAll())
				.thenReturn(List.of(failedBankAccountInformationOneMock, failedBankAccountInformationTwoMock));
		final List<FailedBankAccountInformation> result = testObj.getAll();

		assertThat(result).containsExactlyInAnyOrder(failedBankAccountInformationOneMock,
				failedBankAccountInformationTwoMock);
	}

	@Test
	void deleteByShopId_deleteShopIdReceivedAsParameter() {
		when(failedBankAccountInformationRepositoryMock.deleteByShopId(SHOP_1)).thenReturn(1);

		testObj.deleteByShopId(SHOP_1);

		verify(failedBankAccountInformationRepositoryMock).deleteByShopId(SHOP_1);
		assertThat(logTrackerStub.contains(
				"Retry Process: ShopId " + SHOP_1 + " information was successfully created/updated after retrying."))
						.isTrue();
	}

	@Test
	void findByShopId_shouldFindByShopIdUsingInformationRepository() {
		when(failedBankAccountInformationRepositoryMock.findByShopId(SHOP_1))
				.thenReturn(Arrays.asList(failedBankAccountInformationOneMock, failedBankAccountInformationTwoMock));

		final List<FailedBankAccountInformation> result = testObj.findByShopId(SHOP_1);

		assertThat(result).containsExactlyInAnyOrder(failedBankAccountInformationOneMock,
				failedBankAccountInformationTwoMock);
	}

}
