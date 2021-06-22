package com.paypal.sellers.service;

import com.paypal.sellers.entity.AbstractFailedShopInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractHyperwalletRetryAPIStrategyTest {

	private static final String SHOP_ID = "2001";

	@InjectMocks
	private MyAbstractHyperwalletRetryAPIStrategy testObj;

	@Mock
	private FailedEntityInformationService<AbstractFailedShopInformation> failedEntityInformationService;

	@Mock
	private AbstractFailedShopInformation abstractFailedShopInformationMock;

	@Test
	void includeIntoRetryProcess_shouldSaveShopIdWhenTrueIsReceivedAsParameter() {
		when(failedEntityInformationService.findByShopId(SHOP_ID)).thenReturn(Collections.emptyList());
		testObj.executeRetryProcess(SHOP_ID, Boolean.TRUE);

		verify(failedEntityInformationService).save(SHOP_ID);
	}

	@Test
	void includeIntoRetryProcess_shouldNotSaveShopIdWhenTrueIsReceivedAsParameterAndShopIdExistsInRetryProcess() {
		when(failedEntityInformationService.findByShopId(SHOP_ID))
				.thenReturn(List.of(abstractFailedShopInformationMock));
		testObj.executeRetryProcess(SHOP_ID, Boolean.TRUE);

		verify(failedEntityInformationService, never()).save(SHOP_ID);
	}

	@Test
	void includeIntoRetryProcess_shouldDeleteShopIdWhenFalseIsReceivedAsParameter() {

		testObj.executeRetryProcess(SHOP_ID, Boolean.FALSE);

		verify(failedEntityInformationService).deleteByShopId(SHOP_ID);
	}

	private static class MyAbstractHyperwalletRetryAPIStrategy extends AbstractHyperwalletRetryAPIStrategy {

		protected MyAbstractHyperwalletRetryAPIStrategy(
				final FailedEntityInformationService failedEntityInformationService) {
			super(failedEntityInformationService);
		}

	}

}
