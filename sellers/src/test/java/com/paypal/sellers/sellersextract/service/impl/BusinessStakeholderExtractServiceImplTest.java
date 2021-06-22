package com.paypal.sellers.sellersextract.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderConstants;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletBusinessStakeHolderServiceStrategyFactorySingle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholderExtractServiceImplTest {

	private static final String CLIENT_ID_1 = "clientId1";

	private static final String CLIENT_ID_2 = "clientId2";

	private static final String CLIENT_ID_3 = "clientId3";

	private static final String TOKEN_1 = "token1";

	private static final String TOKEN_2 = "token2";

	private static final String TOKEN_3 = "token3";

	private static final String BUSINESS_STAKE_HOLDER_TOKEN_1 = "businessStakeHolderToken1";

	private static final String BUSINESS_STAKE_HOLDER_TOKEN_2 = "businessStakeHolderToken2";

	private static final String BUSINESS_STAKE_HOLDER_TOKEN_3 = "businessStakeHolderToken3";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	private static final String STK_CREATED_TOKEN_1 = "STK_CREATED_TOKEN1";

	private static final String STK_CREATED_TOKEN_2 = "STK_CREATED_TOKEN2";

	private static final String STK_CREATED_TOKEN_3 = "STK_CREATED_TOKEN3";

	@InjectMocks
	private BusinessStakeholderExtractServiceImpl testObj;

	@Mock
	private SellerModel sellerModelOneMock, sellerModelTwoMock, sellerModelThreeMock,
			sellerModelOneWithBusinessStakeHolderWithTokenMock, sellerModelTwoWithBusinessStakeHolderWithTokenMock,
			sellerModelThreeWithBusinessStakeHolderWithTokenMock;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelOneMock, businessStakeHolderModelTwoMock,
			businessStakeHolderModelThreeMock, businessStakeHolderModelOneMockWithBusinessStakeHolderToken,
			businessStakeHolderModelTwoMockWithBusinessStakeHolderToken,
			businessStakeHolderModelThreeMockWithBusinessStakeHolderToken;

	@Mock
	private Converter<BusinessStakeHolderModel, HyperwalletBusinessStakeholder> businessStakeHolderModelHyperwalletBusinessStakeholderConverterMock;

	@Mock
	private HyperwalletBusinessStakeholder hyperwalletBusinessStakeholderOneMock, hyperwalletBusinessStakeholderTwoMock,
			hyperwalletBusinessStakeholderThreeMock, hyperwalletBusinessStakeholderCreatedOneMock,
			hyperwalletBusinessStakeholderCreatedTwoMock, hyperwalletBusinessStakeholderCreatedThreeMock;

	@Mock
	private HyperwalletUser hyperwalletUserOneMock, hyperwalletUserTwoMock, hyperwalletUserThreeMock;

	@Mock
	private Hyperwallet hyperwalletClientMock;

	@Mock
	private BusinessStakeHolderModel.BusinessStakeHolderModelBuilder businessStakeHolderBuilderOneMock,
			businessStakeHolderBuilderTwoMock, businessStakeHolderBuilderThreeMock;

	@Mock
	private SellerModel.SellerModelBuilder sellerModelBuilderOneMock, sellerModelBuilderTwoMock,
			sellerModelBuilderThreeMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private HyperWalletBusinessStakeHolderServiceStrategyFactorySingle hyperWalletBusinessStakeHolderServiceStrategyFactoryMock;

	@Mock
	private MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractServiceMock;

	@Test
	void extractBusinessStakeHolders_shouldReturnAllBusinessStakeHoldersWithDifferentSellers() {

		final BusinessStakeHolderModel stkOne = BusinessStakeHolderModel.builder().userToken(TOKEN_1)
				.clientUserId("0001").token("STK1_TOKEN")
				.firstName(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						BusinessStakeHolderConstants.FIRST_NAME, "john")), 1)
				.build();
		final BusinessStakeHolderModel stkTwo = BusinessStakeHolderModel.builder().userToken(TOKEN_2)
				.firstName(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						BusinessStakeHolderConstants.FIRST_NAME, "susan")), 1)
				.clientUserId("0002").token("STK2_TOKEN").build();

		final SellerModel sellerModelOne = SellerModel.builder().token(TOKEN_1)
				.businessStakeHolderDetails(List.of(stkOne)).clientUserId(CLIENT_ID_1).build();
		final SellerModel sellerModelTwo = SellerModel.builder().token(TOKEN_2)
				.businessStakeHolderDetails(List.of(stkTwo)).clientUserId(CLIENT_ID_2).build();

		final BusinessStakeHolderModel createdStkOneMock = stkOne.toBuilder().justCreated(true).build();
		final BusinessStakeHolderModel updatedStkTwoMock = stkTwo.toBuilder().build();

		when(hyperWalletBusinessStakeHolderServiceStrategyFactoryMock.execute(stkOne)).thenReturn(createdStkOneMock);
		when(hyperWalletBusinessStakeHolderServiceStrategyFactoryMock.execute(stkTwo)).thenReturn(updatedStkTwoMock);

		testObj.extractBusinessStakeHolders(List.of(sellerModelOne, sellerModelTwo));

		verify(hyperWalletBusinessStakeHolderServiceStrategyFactoryMock).execute(stkOne);
		verify(hyperWalletBusinessStakeHolderServiceStrategyFactoryMock).execute(stkTwo);

		verify(miraklBusinessStakeholderExtractServiceMock).updateBusinessStakeholderToken("0001",
				List.of(createdStkOneMock));
	}

}
