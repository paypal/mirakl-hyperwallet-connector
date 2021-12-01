package com.paypal.sellers.service.impl;

import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.repository.FailedSellersInformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FailedSellersInformationServiceImplTest {

	@Spy
	@InjectMocks
	private FailedSellersInformationServiceImpl testObj;

	@Mock
	private FailedSellersInformationRepository failedSellersInformationRepositoryMock;

	@Mock
	private FailedSellersInformation failedSellersInformationOneMock, failedSellersInformationTwoMock;

	@Captor
	ArgumentCaptor<List<FailedSellersInformation>> failedSellersInformationEntityListArgumentCaptor;

	@Captor
	ArgumentCaptor<String> failedSellersInformationEntityArgumentCaptor;

	@Test
	void saveAll_shouldSaveAllShopsIntoDatabase_whenShopsAreReceived() {
		testObj.saveAll(List.of("shop1", "shop2"));

		verify(failedSellersInformationRepositoryMock)
				.saveAll(failedSellersInformationEntityListArgumentCaptor.capture());
		final List<FailedSellersInformation> shopIds = failedSellersInformationEntityListArgumentCaptor.getValue();
		assertThat(shopIds).hasSize(2);
	}

	@Test
	void save_shouldSaveShopIdIntoDatabase_whenShopIdIsReceived() {
		testObj.save("shop1");

		verify(failedSellersInformationRepositoryMock)
				.saveAll(failedSellersInformationEntityListArgumentCaptor.capture());
		final List<FailedSellersInformation> shopIds = failedSellersInformationEntityListArgumentCaptor.getValue();
		assertThat(shopIds).hasSize(1);
	}

	@Test
	void saveAll_shouldDoNothing_whenNullIsReceivedAsParameter() {
		testObj.saveAll(null);

		verifyNoInteractions(failedSellersInformationRepositoryMock);
	}

	@Test
	void getAll_shouldGetAllFailedSellersInformationEntity() {
		when(failedSellersInformationRepositoryMock.findAll())
				.thenReturn(List.of(failedSellersInformationOneMock, failedSellersInformationTwoMock));
		final List<FailedSellersInformation> result = testObj.getAll();

		assertThat(result).containsExactlyInAnyOrder(failedSellersInformationOneMock, failedSellersInformationTwoMock);
	}

	@Test
	void delete_deleteShopIdReceivedAsParameter() {
		testObj.deleteByShopId("shop1");

		verify(failedSellersInformationRepositoryMock)
				.deleteByShopId(failedSellersInformationEntityArgumentCaptor.capture());
		final String failedSellersInformation = failedSellersInformationEntityArgumentCaptor.getValue();
		assertThat(failedSellersInformation).isEqualTo("shop1");
	}

}
