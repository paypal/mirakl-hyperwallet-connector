package com.paypal.infrastructure.mirakl.client.filter;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class IgnoredShopsFilterTest {

	@InjectMocks
	private IgnoredShopsFilter testObj;

	@Mock
	private HyperwalletProgramsConfiguration hyperwalletProgramsConfigurationMock;

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
	void getShops_shouldFilterShopsBelongingToIgnoredPrograms() {
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShop1Mock, miraklShop2Mock, miraklShop3Mock));
		mockShop(miraklShop1Mock, 1);
		mockShop(miraklShop2Mock, 2);
		mockShop(miraklShop3Mock, 3);
		mockIgnoreProgram(1, false);
		mockIgnoreProgram(2, true);
		mockIgnoreProgram(3, false);

		testObj.filterShops(miraklShopsMock);

		verify(miraklShopsMock, times(1)).setShops(argThat(x -> x.size() == 2));
		verify(miraklShopsMock, times(1))
				.setShops(argThat(x -> x.containsAll(List.of(miraklShop1Mock, miraklShop3Mock))));
	}

	private void mockShop(final MiraklShop miraklShopMock, final int id) {
		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue miraklAdditionalFieldValueMock = Mockito
				.mock(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class);
		lenient().when(miraklShopMock.getId()).thenReturn(String.valueOf(id));
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(List.of(miraklAdditionalFieldValueMock));
		when(miraklAdditionalFieldValueMock.getCode()).thenReturn("hw-program");
		when(miraklAdditionalFieldValueMock.getValue()).thenReturn("PROGRAM-" + id);
	}

	private void mockIgnoreProgram(final int id, final boolean ignored) {
		final HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration programConfiguration = Mockito
				.mock(HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration.class);
		lenient().when(hyperwalletProgramsConfigurationMock.getProgramConfiguration("PROGRAM-" + id))
				.thenReturn(programConfiguration);
		lenient().when(programConfiguration.isIgnored()).thenReturn(ignored);
	}

}
