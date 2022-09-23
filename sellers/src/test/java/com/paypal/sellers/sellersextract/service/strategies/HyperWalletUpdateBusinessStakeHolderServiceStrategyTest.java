package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletUpdateBusinessStakeHolderServiceStrategyTest {

	@InjectMocks
	private HyperWalletUpdateBusinessStakeHolderServiceStrategy testObj;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderMock;

	@Mock
	private Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock;

	@Mock
	private HyperwalletBusinessStakeholder hyperwalletBusinessStakeholderMock,
			hyperwalletBusinessStakeholderResponseMock;

	@Mock
	private Hyperwallet hyperwalletClientMock;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserService;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	private static final String CLIENT_ID = "clientID";

	private static final String TOKEN = "token";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Test
	void execute_shouldReturnUpdatedHyperWalletBusinessStakeHolder() {
		when(businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock.convert(businessStakeHolderMock))
				.thenReturn(hyperwalletBusinessStakeholderMock);
		when(hyperwalletClientMock.updateBusinessStakeholder(TOKEN, hyperwalletBusinessStakeholderMock))
				.thenReturn(hyperwalletBusinessStakeholderResponseMock);
		when(businessStakeHolderMock.getUserToken()).thenReturn(TOKEN);
		when(businessStakeHolderMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(hyperwalletSDKUserService.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletClientMock);

		final BusinessStakeHolderModel result = testObj.execute(businessStakeHolderMock);

		assertThat(result).isEqualTo(businessStakeHolderMock);
	}

	@Test
	void execute_shouldSendEmailNotificationHyperwalletExceptionIsThrown() {
		when(businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock.convert(businessStakeHolderMock))
				.thenReturn(hyperwalletBusinessStakeholderMock);
		when(businessStakeHolderMock.getUserToken()).thenReturn(TOKEN);
		when(businessStakeHolderMock.getClientUserId()).thenReturn(CLIENT_ID);
		when(businessStakeHolderMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(hyperwalletSDKUserService.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletClientMock);

		final HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		doThrow(hyperwalletException).when(hyperwalletClientMock).updateBusinessStakeholder(TOKEN,
				hyperwalletBusinessStakeholderMock);

		testObj.execute(businessStakeHolderMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected when updating business stakeholder in Hyperwallet",
				String.format(ERROR_MESSAGE_PREFIX + "Business stakeholder not updated for clientId [%s]%n%s",
						CLIENT_ID, HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void isApplicable_shouldReturnFalseWhenBusinessStakeHolderHasAnEmptyToken() {
		final boolean result = testObj.isApplicable(businessStakeHolderMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBusinessStakeHolderHasAToken() {
		when(businessStakeHolderMock.getToken()).thenReturn("TOKEN");

		final boolean result = testObj.isApplicable(businessStakeHolderMock);

		assertThat(result).isTrue();
	}

}
