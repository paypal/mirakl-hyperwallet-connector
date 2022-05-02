package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHyperwalletSellerServiceStrategyTest {

	public static final String USR_TOKEN = "usr-1200210";

	@Spy
	@InjectMocks
	private MyAbstractHyperwalletSellerServiceStrategy testObj;

	@Mock
	private Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverterMock;

	private SellerModel sellerModelStub;

	@Mock
	private HyperwalletUser hyperwalletUserStub;

	@BeforeEach
	void setUp() {
		sellerModelStub = SellerModel.builder().clientUserId("2000").build();
		hyperwalletUserStub = new HyperwalletUser();
		hyperwalletUserStub.setToken(USR_TOKEN);
	}

	@Test
	void execute_shouldConvertTheSellerModelIntoHyperwalletUserCallHyperwalletAndReturnAnUpdatedSellerModelWithTheTokenFromHyperwalletCall() {
		when(sellerModelHyperwalletUserConverterMock.convert(sellerModelStub)).thenReturn(hyperwalletUserStub);
		when(testObj.pushToHyperwallet(hyperwalletUserStub)).thenReturn(hyperwalletUserStub);

		final SellerModel result = testObj.execute(sellerModelStub);

		verify(testObj).pushToHyperwallet(hyperwalletUserStub);
		assertThat(result.getToken()).isEqualTo(USR_TOKEN);
	}

	private static class MyAbstractHyperwalletSellerServiceStrategy extends AbstractHyperwalletSellerServiceStrategy {

		protected MyAbstractHyperwalletSellerServiceStrategy(
				final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
				final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
			super(sellerModelHyperwalletUserConverter, hyperwalletSDKService, mailNotificationUtil);
		}

		@Override
		protected HyperwalletUser pushToHyperwallet(final HyperwalletUser hyperwalletUser) {
			return null;
		}

		@Override
		public boolean isApplicable(final SellerModel source) {
			return false;
		}

	}

}
