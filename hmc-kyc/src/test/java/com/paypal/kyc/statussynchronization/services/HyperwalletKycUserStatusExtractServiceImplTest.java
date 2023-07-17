package com.paypal.kyc.statussynchronization.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletKycUserStatusExtractServiceImplTest {

	private HyperwalletKycUserStatusExtractServiceImpl testObj;

	@Mock
	private Converter<HyperwalletUser, KYCUserStatusInfoModel> kycUserStatusInfoModelConverterMock;

	@Mock
	private UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Mock
	private KYCUserStatusInfoModel kycUserStatusInfoModelMock;

	@BeforeEach
	void setUp() {
		when(userHyperwalletSDKServiceMock.getHyperwalletInstance()).thenReturn(hyperwalletMock);
		testObj = new HyperwalletKycUserStatusExtractServiceImpl(userHyperwalletSDKServiceMock,
				kycUserStatusInfoModelConverterMock);
	}

	@Test
	void extractKycUserStatuses_shouldReturnAConvertedListOfKYCUserStatusInfoModel_whenDatesAreCorrect() {
		final List<HyperwalletUser> listOfUsers = List.of(hyperwalletUserMock);
		final HyperwalletList<HyperwalletUser> hyperwalletListOfUsers = new HyperwalletList<>();
		hyperwalletListOfUsers.setData(listOfUsers);

		when(hyperwalletMock.listUsers(any())).thenReturn(hyperwalletListOfUsers);
		when(kycUserStatusInfoModelConverterMock.convert(hyperwalletUserMock)).thenReturn(kycUserStatusInfoModelMock);

		final Date startDate = new Date();
		final Date endDate = new Date();
		final List<KYCUserStatusInfoModel> extractedData = testObj.extractKycUserStatuses(startDate, endDate);
		assertThat(extractedData.get(0)).isEqualTo(kycUserStatusInfoModelMock);
		verify(hyperwalletMock, times(1))
				.listUsers(argThat(x -> x.getCreatedAfter().equals(startDate) && x.getCreatedBefore().equals(endDate)));
	};

}
