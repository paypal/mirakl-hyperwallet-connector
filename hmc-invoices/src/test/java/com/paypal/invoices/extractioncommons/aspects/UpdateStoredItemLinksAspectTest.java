package com.paypal.invoices.extractioncommons.aspects;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.paypal.invoices.extractioncommons.services.AccountingDocumentsLinksService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStoredItemLinksAspectTest {

	@InjectMocks
	private UpdateStoredItemLinksAspect testObj;

	@Mock
	private AccountingDocumentsLinksService accountingDocumentsLinksServiceMock;

	@Test
	void interceptNotificationsMethod_shouldUpdateStoredItemLinks() throws Throwable {
		// given
		final ProceedingJoinPoint proceedingJoinPointMock = mock(ProceedingJoinPoint.class);
		final MiraklShops miraklShopsMock = mock(MiraklShops.class);
		final List<MiraklShop> miraklShopsList = List.of(mock(MiraklShop.class));
		when(miraklShopsMock.getShops()).thenReturn(miraklShopsList);
		when(proceedingJoinPointMock.proceed()).thenReturn(miraklShopsMock);

		// when
		final MiraklShops result = testObj.interceptNotificationMethod(proceedingJoinPointMock);

		// then
		verify(accountingDocumentsLinksServiceMock).updateLinksFromShops(miraklShopsList);
		assertThat(result).isEqualTo(miraklShopsMock);
	}

	@Test
	void interceptNotificationsMethod_shouldUpdateStoredItemLinks_andPassEmptyListIfNoShops() throws Throwable {
		// given
		final ProceedingJoinPoint proceedingJoinPointMock = mock(ProceedingJoinPoint.class);
		final MiraklShops miraklShopsMock = mock(MiraklShops.class);
		when(proceedingJoinPointMock.proceed()).thenReturn(miraklShopsMock);

		// when
		final MiraklShops result = testObj.interceptNotificationMethod(proceedingJoinPointMock);

		// then
		verify(accountingDocumentsLinksServiceMock).updateLinksFromShops(List.of());
		assertThat(result).isEqualTo(miraklShopsMock);
	}

	@Test
	void interceptNotificationsMethod_shouldNotDoAnything_ifPjpReturnIsNull() throws Throwable {
		// given
		final ProceedingJoinPoint proceedingJoinPointMock = mock(ProceedingJoinPoint.class);
		when(proceedingJoinPointMock.proceed()).thenReturn(null);

		// when
		final MiraklShops result = testObj.interceptNotificationMethod(proceedingJoinPointMock);

		// then
		verify(accountingDocumentsLinksServiceMock, never()).updateLinksFromShops(any());
		assertThat(result).isNull();
	}

	@Test
	void interceptNotificationsMethod_shouldNotThrowExceptions_whenUpdateStoreFails() throws Throwable {
		// given
		final ProceedingJoinPoint proceedingJoinPointMock = mock(ProceedingJoinPoint.class);
		final MiraklShops miraklShopsMock = mock(MiraklShops.class);
		final List<MiraklShop> miraklShopsList = List.of(mock(MiraklShop.class));
		when(miraklShopsMock.getShops()).thenReturn(miraklShopsList);
		when(proceedingJoinPointMock.proceed()).thenReturn(miraklShopsMock);
		doThrow(new RuntimeException()).when(accountingDocumentsLinksServiceMock).updateLinksFromShops(miraklShopsList);

		// when
		final MiraklShops result = testObj.interceptNotificationMethod(proceedingJoinPointMock);

		// then
		assertThat(result).isEqualTo(miraklShopsMock);
	}

}
