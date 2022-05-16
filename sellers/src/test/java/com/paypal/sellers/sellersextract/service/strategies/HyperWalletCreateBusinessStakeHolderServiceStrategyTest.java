package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletCreateBusinessStakeHolderServiceStrategyTest {

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@InjectMocks
	private HyperWalletCreateBusinessStakeHolderServiceStrategy testObj;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderMock, businessStakeHolderResponseMock;

	@Mock
	private Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock;

	@Mock
	private HyperwalletBusinessStakeholder hyperwalletBusinessStakeholderMock,
			hyperwalletBusinessStakeholderResponseMock;

	@Mock
	private Hyperwallet hyperwalletClientMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private BusinessStakeHolderModel.BusinessStakeHolderModelBuilder businessStakeHolderBuilderMock;

	@Mock
	private MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractServiceMock;

	private static final String CLIENT_ID = "clientID";

	private static final String TOKEN = "token";

	private static final String BUSINESS_STAKE_HOLDER_TOKEN = "stk-token";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Test
	void execute_shouldReturnCreatedHyperWalletBusinessStakeHolder() {
		when(businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock.convert(businessStakeHolderMock))
				.thenReturn(hyperwalletBusinessStakeholderMock);
		when(hyperwalletClientMock.createBusinessStakeholder(TOKEN, hyperwalletBusinessStakeholderMock))
				.thenReturn(hyperwalletBusinessStakeholderResponseMock);
		when(hyperwalletBusinessStakeholderResponseMock.getToken()).thenReturn(BUSINESS_STAKE_HOLDER_TOKEN);
		when(businessStakeHolderMock.getUserToken()).thenReturn(TOKEN);
		when(businessStakeHolderMock.toBuilder()).thenReturn(businessStakeHolderBuilderMock);
		when(businessStakeHolderBuilderMock.token(BUSINESS_STAKE_HOLDER_TOKEN))
				.thenReturn(businessStakeHolderBuilderMock);
		when(businessStakeHolderBuilderMock.justCreated(true)).thenReturn(businessStakeHolderBuilderMock);
		when(businessStakeHolderBuilderMock.build()).thenReturn(businessStakeHolderResponseMock);
		when(businessStakeHolderResponseMock.getClientUserId()).thenReturn(CLIENT_ID);
		when(businessStakeHolderMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);

		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletClientMock);

		final BusinessStakeHolderModel result = testObj.execute(businessStakeHolderMock);

		verify(miraklBusinessStakeholderExtractServiceMock).updateBusinessStakeholderToken(CLIENT_ID,
				List.of(businessStakeHolderResponseMock));

		assertThat(result).isEqualTo(businessStakeHolderResponseMock);
	}

	@Test
	void execute_shouldSendEmailNotificationHyperwalletExceptionIsThrown() {
		when(businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock.convert(businessStakeHolderMock))
				.thenReturn(hyperwalletBusinessStakeholderMock);
		when(businessStakeHolderMock.getClientUserId()).thenReturn(CLIENT_ID);
		when(businessStakeHolderMock.getUserToken()).thenReturn(TOKEN);
		when(businessStakeHolderMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);

		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletClientMock);

		final HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		doThrow(hyperwalletException).when(hyperwalletClientMock).createBusinessStakeholder(TOKEN,
				hyperwalletBusinessStakeholderMock);

		testObj.execute(businessStakeHolderMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected when creating business stakeholder in Hyperwallet",
				String.format(ERROR_MESSAGE_PREFIX + "Business stakeholder not created for clientId [%s]%n%s",
						CLIENT_ID, HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBusinessStakeHolderHasAnEmptyToken() {
		final boolean result = testObj.isApplicable(businessStakeHolderMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenBusinessStakeHolderHasAToken() {
		when(businessStakeHolderMock.getToken()).thenReturn("TOKEN");

		final boolean result = testObj.isApplicable(businessStakeHolderMock);

		assertThat(result).isFalse();
	}

}
