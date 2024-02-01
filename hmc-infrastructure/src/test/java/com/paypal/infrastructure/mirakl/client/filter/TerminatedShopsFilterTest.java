package com.paypal.infrastructure.mirakl.client.filter;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShopState;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TerminatedShopsFilterTest {

	@InjectMocks
	private TerminatedShopsFilter testObj;

	@Mock
	private MiraklShops miraklShopsMock;

	@Mock
	private MiraklShop miraklShop1Mock, miraklShop2Mock, miraklShop3Mock;

	@BeforeEach
	void setUp() {
		final MiraklApiClientConfig config = new MiraklApiClientConfig();
		config.setOperatorApiKey("OPERATOR-KEY");
		config.setEnvironment("environment");
	}

	@Test
	void getShops_shouldFilterShopsHaveStateAsTerminated() {
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShop1Mock, miraklShop2Mock, miraklShop3Mock));
		when(miraklShop1Mock.getState()).thenReturn(MiraklShopState.TERMINATED);
		when(miraklShop2Mock.getState()).thenReturn(MiraklShopState.OPEN);
		when(miraklShop3Mock.getState()).thenReturn(MiraklShopState.SUSPENDED);

		testObj.filterShops(miraklShopsMock);

		verify(miraklShopsMock, times(1)).setShops(argThat(x -> x.size() == 2));
		verify(miraklShopsMock, times(1))
				.setShops(argThat(x -> x.containsAll(List.of(miraklShop2Mock, miraklShop3Mock))));
	}

}
