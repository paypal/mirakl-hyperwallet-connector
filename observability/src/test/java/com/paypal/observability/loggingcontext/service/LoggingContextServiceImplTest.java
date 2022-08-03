package com.paypal.observability.loggingcontext.service;

import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import com.paypal.observability.loggingcontext.service.serializer.LoggingTransactionSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingContextServiceImplTest {

	@InjectMocks
	private LoggingContextServiceImpl testObj;

	@Mock
	private LoggingContextHolder loggingContextHolderMock;

	@Mock
	private LoggingTransactionSerializer loggingTransactionSerializerMock;

	@Mock
	private LoggingTransaction businessTransactionMock;

	private static MockedStatic<MDC> mdcMockedStatic;

	@BeforeAll
	static void setUp() {
		mdcMockedStatic = Mockito.mockStatic(MDC.class);
	}

	@Test
	void getCurrentLoggingTransaction_shouldReturnGetCurrentBusinessTransactionResult() {
		when(loggingContextHolderMock.getCurrentBusinessTransaction()).thenReturn(Optional.of(businessTransactionMock));

		final Optional<LoggingTransaction> result = testObj.getCurrentLoggingTransaction();

		assertThat(result).isEqualTo(Optional.of(businessTransactionMock));
	}

	@Test
	void updateLoggingTransaction_shouldCallRefreshBusinessTransactionAndPutTheTransactionIntoTheMDCAsString() {
		final String serializedBusinessTransaction = "{id:102230, subtype:jobName}";
		when(loggingTransactionSerializerMock.serialize(businessTransactionMock))
				.thenReturn(serializedBusinessTransaction);

		testObj.updateLoggingTransaction(businessTransactionMock);

		verify(loggingContextHolderMock).refreshBusinessTransaction(businessTransactionMock);
		mdcMockedStatic.verify(() -> MDC.put("businessTransaction", serializedBusinessTransaction));
	}

	@Test
	void closeLoggingTransactionShouldCloseBusinessTransactionAndClearMDC() {

		testObj.closeLoggingTransaction();

		verify(loggingContextHolderMock).closeBusinessTransaction();
		mdcMockedStatic.verify(MDC::clear);
	}

}
