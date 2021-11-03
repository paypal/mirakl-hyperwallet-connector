package com.paypal.sellers.bankaccountextract.service.impl;

import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.sellers.bankaccountextract.service.strategies.HyperWalletBankAccountServiceStrategyExecutor;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import com.paypal.sellers.service.FailedEntityInformationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountExtractServiceImplTest {

	@InjectMocks
	private BankAccountExtractServiceImpl testObj;

	@Mock
	private FailedEntityInformationService<FailedBankAccountInformation> failedBankAccountInformationFailedEntityInformationServiceMock;

	@Mock
	private FailedBankAccountInformation failedBankAccountInformationMock;

	@Mock
	private SellerModel failedSellerModelMock, sellerModelMock;

	@Mock
	private HyperWalletBankAccountServiceStrategyExecutor hyperWalletBankAccountServiceStrategyExecutorMock;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Test
	void extractBankAccounts_shouldExtractMiraklBankAccountInformationFromFailedSellersAndUpdatedSellers() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		final LocalDateTime now = TimeMachine.now();
		final Date nowAsDate = DateUtil.convertToDate(now, ZoneId.systemDefault());
		when(failedBankAccountInformationFailedEntityInformationServiceMock.getAll())
				.thenReturn(List.of(failedBankAccountInformationMock));
		when(failedBankAccountInformationMock.getShopId()).thenReturn("2001");
		when(miraklSellersExtractServiceMock.extractSellers(List.of("2001")))
				.thenReturn(List.of(failedSellerModelMock));
		when(miraklSellersExtractServiceMock.extractSellers(nowAsDate)).thenReturn(List.of(sellerModelMock));

		testObj.extractBankAccounts(nowAsDate);

		verify(hyperWalletBankAccountServiceStrategyExecutorMock).execute(sellerModelMock);
		verify(hyperWalletBankAccountServiceStrategyExecutorMock).execute(failedSellerModelMock);
	}

}
