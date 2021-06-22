package com.paypal.sellers.service.impl;

import com.paypal.sellers.bankaccountextract.service.impl.FailedBankAccountInformationServiceImpl;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.repository.FailedBankAccountInformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FailedBankAccountInformationServiceImplTest {

	@InjectMocks
	private FailedBankAccountInformationServiceImpl testObj;

	@Mock
	private FailedBankAccountInformationRepository failedBankAccountInformationRepositoryMock;

	@Mock
	private FailedBankAccountInformation failedBankAccountInformationOneMock, failedBankAccountInformationTwoMock;

	@Captor
	ArgumentCaptor<List<FailedBankAccountInformation>> failedBankAccountInformationArgumentCaptor;

	@Captor
	ArgumentCaptor<String> failedBankAccountInformationEntityArgumentCaptor;

	@Test
	void saveAll_shouldSaveAllShopsIntoDatabase_whenShopsAreReceived() {
		testObj.saveAll(List.of("shop1", "shop2"));

		verify(failedBankAccountInformationRepositoryMock)
				.saveAll(failedBankAccountInformationArgumentCaptor.capture());
		final List<FailedBankAccountInformation> shopIds = failedBankAccountInformationArgumentCaptor.getValue();
		assertThat(shopIds).hasSize(2);
	}

	@Test
	void save_shouldSaveShopIdIntoDatabase_whenShopIdIsReceived() {
		testObj.save("shop1");

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
	void delete_deleteShopIdReceivedAsParameter() {

		testObj.deleteByShopId("shop1");

		verify(failedBankAccountInformationRepositoryMock)
				.deleteByShopId(failedBankAccountInformationEntityArgumentCaptor.capture());
		final String failedSellersInformation = failedBankAccountInformationEntityArgumentCaptor.getValue();
		assertThat(failedSellersInformation).isEqualTo("shop1");
	}

}
