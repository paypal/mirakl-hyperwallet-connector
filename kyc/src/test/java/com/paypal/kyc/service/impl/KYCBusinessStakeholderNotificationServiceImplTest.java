package com.paypal.kyc.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.strategies.status.impl.KYCBusinessStakeholderStatusExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCBusinessStakeholderNotificationServiceImplTest {

	private static final String MSG_EXCEPTION = "An error occurred";

	private static final String MSG_ERROR = "Notification [%s] could not be processed - the KYC Letter of authorization for a business stakeholder could not be updated.%n%s";

	private static final LogTrackerStub LOG_TRACKER_STUB = LogTrackerStub.create()
			.recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(KYCBusinessStakeholderNotificationServiceImpl.class);

	@InjectMocks
	private KYCBusinessStakeholderNotificationServiceImpl testObj;

	@Mock
	private Converter<Object, KYCBusinessStakeholderStatusNotificationBodyModel> hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock;

	@Mock
	private KYCBusinessStakeholderStatusExecutor kycBusinessStakeholderStatusExecutorMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderNotificationMock;

	@BeforeEach
	void setUp() {
		when(hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock
				.convert(hyperwalletWebhookNotificationMock)).thenReturn(kycBusinessStakeholderNotificationMock);
	}

	@Test
	void updateBusinessStakeholderKYCStatus_shouldConvertAndTreatThenIncomingNotification() {
		testObj.updateBusinessStakeholderKYCStatus(hyperwalletWebhookNotificationMock);

		verify(hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock)
				.convert(hyperwalletWebhookNotificationMock);
		verify(kycBusinessStakeholderStatusExecutorMock).execute(kycBusinessStakeholderNotificationMock);
	}

	@ParameterizedTest
	@MethodSource("exceptionAndExpectedMessage")
	void updateBusinessStakeholderKYCStatus_whenMiraklExceptionIsThrown_shouldLogAndRethrowException(
			final RuntimeException exception, final String expectedMessage) {
		when(kycBusinessStakeholderStatusExecutorMock.execute(kycBusinessStakeholderNotificationMock))
				.thenThrow(exception);

		final Throwable throwable = catchThrowable(
				() -> testObj.updateBusinessStakeholderKYCStatus(hyperwalletWebhookNotificationMock));

		assertThat(throwable).isEqualTo(exception);
		assertThat(LOG_TRACKER_STUB
				.contains(String.format(MSG_ERROR, hyperwalletWebhookNotificationMock.getToken(), expectedMessage)))
						.isTrue();
		verify(hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock)
				.convert(hyperwalletWebhookNotificationMock);
		verify(kycBusinessStakeholderStatusExecutorMock).execute(kycBusinessStakeholderNotificationMock);
	}

	private static Stream<Arguments> exceptionAndExpectedMessage() {
		final HyperwalletException hyperwalletException = new HyperwalletException(MSG_EXCEPTION);
		final MiraklException miraklException = new MiraklException(MSG_EXCEPTION);
		return Stream.of(Arguments.of(new RuntimeException(MSG_EXCEPTION), MSG_EXCEPTION),
				Arguments.of(hyperwalletException, HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)),
				Arguments.of(miraklException, MiraklLoggingErrorsUtil.stringify(miraklException)));
	}

}
