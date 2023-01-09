package com.paypal.infrastructure.sdk.mirakl.impl;

import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MiraklMarketplacePlatformOperatorApiClientWrapperImplTest {

	private MiraklMarketplacePlatformOperatorApiClientWrapperImpl testObj;

	@Mock
	private HMCMiraklInvoices hmcMiraklInvoicesMock;

	@Mock
	private MiraklGetInvoicesRequest miraklGetInvoicesRequestMock;

	@BeforeEach
	void setUp() {
		final MiraklApiClientConfig config = new MiraklApiClientConfig();
		config.setOperatorApiKey("OPERATOR-KEY");
		config.setEnvironment("environment");
		testObj = Mockito.spy(new MiraklMarketplacePlatformOperatorApiClientWrapperImpl(config));
	}

	@Test
	void getInvoices_shouldCallSDKGetInvoicesMethod() {
		doReturn(hmcMiraklInvoicesMock).when(testObj).getHmcMiraklInvoices(miraklGetInvoicesRequestMock);

		final HMCMiraklInvoices invoices = testObj.getInvoices(miraklGetInvoicesRequestMock);

		assertThat(invoices).isEqualTo(hmcMiraklInvoicesMock);
	}

}
