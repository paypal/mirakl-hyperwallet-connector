package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShops;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.service.ChangeStagingService;
import com.paypal.infrastructure.mirakl.client.converters.MiraklStageChangeConverter;
import com.paypal.infrastructure.mirakl.client.filter.IgnoredShopsFilter;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import com.paypal.infrastructure.mirakl.settings.MiraklClientSettings;
import com.paypal.infrastructure.mirakl.settings.MiraklClientSettingsExecutor;
import com.paypal.infrastructure.mirakl.settings.MiraklClientSettingsHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageChangesMiraklClientTest {

	private StageChangesMiraklClient testObj;

	@Mock
	private ChangeStagingService changeStagingServiceMock;

	@Mock
	private MiraklStageChangeConverter miraklStageChangeConverterMock;

	@BeforeEach
	void setUp() {
		final MiraklApiClientConfig config = new MiraklApiClientConfig();
		config.setOperatorApiKey("OPERATOR-KEY");
		config.setEnvironment("environment");
		testObj = Mockito.spy(new StageChangesMiraklClient(config, mock(IgnoredShopsFilter.class),
				changeStagingServiceMock, miraklStageChangeConverterMock));
	}

	@AfterEach
	void tearDown() {
		MiraklClientSettingsHolder.clear();
	}

	@Test
	void updateShops_shouldStageChanges_whenStagingIsEnabled() {
		// given
		final MiraklUpdateShopsRequest miraklUpdateShopsRequest = mock(MiraklUpdateShopsRequest.class);
		final List<MiraklUpdateShop> miraklUpdateShops = List.of(mock(MiraklUpdateShop.class));
		final List<Change> changes = List.of(mock(Change.class));
		when(miraklUpdateShopsRequest.getShops()).thenReturn(miraklUpdateShops);
		when(miraklStageChangeConverterMock.from(miraklUpdateShops)).thenReturn(changes);
		MiraklClientSettingsHolder.setMiraklClientSettings(new MiraklClientSettings(true));

		// when
		final MiraklUpdatedShops result = testObj.updateShops(miraklUpdateShopsRequest);

		// then
		assertThat(result.getShopReturns()).isEmpty();
		verify(changeStagingServiceMock).stageChanges(changes);
		verify(testObj, never()).callSuperGetMiraklUpdatedShops(miraklUpdateShopsRequest);
	}

	@Test
	void updateShops_shouldDelegateOnMiraklSDK_whenStagingIsDisabled() {
		// given
		final MiraklUpdateShopsRequest miraklUpdateShopsRequest = mock(MiraklUpdateShopsRequest.class);
		MiraklClientSettingsHolder.setMiraklClientSettings(new MiraklClientSettings(false));
		final MiraklUpdatedShops miraklUpdatedShops = mock(MiraklUpdatedShops.class);
		doReturn(miraklUpdatedShops).when(testObj).callSuperGetMiraklUpdatedShops(miraklUpdateShopsRequest);

		// when
		final MiraklUpdatedShops result = testObj.updateShops(miraklUpdateShopsRequest);

		// then
		assertThat(result).isEqualTo(miraklUpdatedShops);
		verify(changeStagingServiceMock, never()).stageChanges(any());
		verify(testObj).callSuperGetMiraklUpdatedShops(miraklUpdateShopsRequest);
	}

}
