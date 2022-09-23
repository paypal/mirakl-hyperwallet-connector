package com.paypal.sellers.sellersextract.service.impl;

import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholderTokenSynchronizationServiceImplTest {

	private static final String EMAIL = "hw-stakeholder-email-1";

	private static final String CLIENT_USER_ID = "1234";

	private static final String STK_TOKEN = "89f7f89";

	private static final int STK_ID = 1;

	private static final String STK_EMAIL = "test@test.com";

	public static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	public static final String USER_TOKEN = "userToken";

	@Spy
	@InjectMocks
	private BusinessStakeholderTokenSynchronizationServiceImpl testObj;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractServiceMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create()
			.recordForType(BusinessStakeholderTokenSynchronizationServiceImpl.class);

	@Mock
	private Hyperwallet hyperwalletMock;

	@Captor
	private ArgumentCaptor<List<BusinessStakeHolderModel>> businessStakeHolderModelsArgumentCaptor;

	@Test
	void synchronizeToken_ShouldSendAnEmailAndThrowAnHMCException_WhenStkEmailIsMandatoryAndSTKEmailIsNullAndSTKTokenIsNull() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).stkId(STK_ID).build();

		doReturn(true).when(testObj).isStkEmailMandatory();

		assertThatThrownBy(() -> testObj.synchronizeToken(businessStakeHolderModel)).isInstanceOf(HMCException.class)
				.hasMessage("Business stakeholder without email and Hyperwallet token defined for shop ["
						+ CLIENT_USER_ID + "]");

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Validation error occurred when processing a stakeholder for seller {" + CLIENT_USER_ID + "}",
				"There was an error processing the {" + CLIENT_USER_ID
						+ "} seller and the operation could not be completed. Email address for the stakeholder {"
						+ STK_ID + "} must be filled.\n" + "Please check the logs for further information.");
	}

	@Test
	void synchronizeToken_ShouldReturnTheCurrentSTK_WhenSTKTokenIsNotBlank() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).token(STK_TOKEN).build();

		final BusinessStakeHolderModel result = testObj.synchronizeToken(businessStakeHolderModel);

		assertThat(result).isEqualTo(businessStakeHolderModel);
		assertThat(logTrackerStub.contains("Hyperwallet token already exists for business stakeholder [" + STK_TOKEN
				+ "] for shop [" + CLIENT_USER_ID + "], synchronization not needed")).isTrue();
	}

	@Test
	void synchronizeToken_ShouldReturnTheCurrentSTK_WhenSTKTokenIsNullAndEmailIsNullAndStkEmailIsNotMandatory() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).stkId(STK_ID).build();

		doReturn(false).when(testObj).isStkEmailMandatory();

		final BusinessStakeHolderModel result = testObj.synchronizeToken(businessStakeHolderModel);

		assertThat(result).isEqualTo(businessStakeHolderModel);
		assertThat(logTrackerStub.contains(
				"Hyperwallet business stakeholder [" + STK_ID + "] for shop [" + CLIENT_USER_ID + "] not found"))
						.isTrue();
	}

	@Test
	void synchronizeToken_ShouldThrowAnHMCHyperwalletAPIException_WhenGettingHwBusinessStakeholdersAnHyperwalletExceptionIsThrown() {

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue emailBusinessStakeHolderField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		emailBusinessStakeHolderField.setCode(EMAIL);
		emailBusinessStakeHolderField.setValue(STK_EMAIL);

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder().stkId(STK_ID)
				.hyperwalletProgram(HYPERWALLET_PROGRAM).userToken(USER_TOKEN).clientUserId(CLIENT_USER_ID)
				.email(List.of(emailBusinessStakeHolderField), STK_ID).build();

		doReturn(false).when(testObj).isStkEmailMandatory();

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.listBusinessStakeholders(USER_TOKEN)).thenThrow(HyperwalletException.class);

		assertThatThrownBy(() -> testObj.synchronizeToken(businessStakeHolderModel))
				.isInstanceOf(HMCHyperwalletAPIException.class);
		assertThat(logTrackerStub
				.contains("Error while getting Hyperwallet business stakeholders for shop [" + CLIENT_USER_ID + "]"))
						.isTrue();
	}

	@Test
	void synchronizeToken_ShouldThrowAnHMCMiraklAPIException_WhenUpdatingSTKTokenInMiraklThrowsAMiraklException() {

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue emailBusinessStakeHolderField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		emailBusinessStakeHolderField.setCode(EMAIL);
		emailBusinessStakeHolderField.setValue(STK_EMAIL);

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder().stkId(STK_ID)
				.hyperwalletProgram(HYPERWALLET_PROGRAM).userToken(USER_TOKEN).clientUserId(CLIENT_USER_ID)
				.email(List.of(emailBusinessStakeHolderField), STK_ID).build();

		doReturn(false).when(testObj).isStkEmailMandatory();

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletMock);

		final HyperwalletBusinessStakeholder hyperwalletBusinessStakeholder = new HyperwalletBusinessStakeholder();
		hyperwalletBusinessStakeholder.setToken(STK_TOKEN);
		hyperwalletBusinessStakeholder.setEmail(STK_EMAIL);
		final HyperwalletList<HyperwalletBusinessStakeholder> hyperwalletBusinessStakeholders = new HyperwalletList<>();
		hyperwalletBusinessStakeholders.setData(List.of(hyperwalletBusinessStakeholder));
		when(hyperwalletMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(hyperwalletBusinessStakeholders);

		doThrow(MiraklException.class).when(miraklBusinessStakeholderExtractServiceMock)
				.updateBusinessStakeholderToken(eq(CLIENT_USER_ID), anyList());

		assertThatThrownBy(() -> testObj.synchronizeToken(businessStakeHolderModel))
				.isInstanceOf(HMCMiraklAPIException.class);
		assertThat(logTrackerStub.contains("Error while updating Mirakl business stakeholder [" + STK_TOKEN
				+ "] for shop [" + CLIENT_USER_ID + "]")).isTrue();
	}

	@Test
	void synchronizeToken_ShouldRetrieveSTKFromHyperwalletAndSynchronizedTokenInMirakl_WhenEmailIsNotBlankAndSTKTokenIsEmpty() {

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue emailBusinessStakeHolderField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		emailBusinessStakeHolderField.setCode(EMAIL);
		emailBusinessStakeHolderField.setValue(STK_EMAIL);

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder().stkId(STK_ID)
				.hyperwalletProgram(HYPERWALLET_PROGRAM).userToken(USER_TOKEN).clientUserId(CLIENT_USER_ID)
				.email(List.of(emailBusinessStakeHolderField), STK_ID).build();

		doReturn(false).when(testObj).isStkEmailMandatory();

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletMock);

		final HyperwalletBusinessStakeholder hyperwalletBusinessStakeholder = new HyperwalletBusinessStakeholder();
		hyperwalletBusinessStakeholder.setToken(STK_TOKEN);
		hyperwalletBusinessStakeholder.setEmail(STK_EMAIL);
		final HyperwalletList<HyperwalletBusinessStakeholder> hyperwalletBusinessStakeholders = new HyperwalletList<>();
		hyperwalletBusinessStakeholders.setData(List.of(hyperwalletBusinessStakeholder));
		when(hyperwalletMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(hyperwalletBusinessStakeholders);

		final BusinessStakeHolderModel result = testObj.synchronizeToken(businessStakeHolderModel);

		verify(miraklBusinessStakeholderExtractServiceMock).updateBusinessStakeholderToken(eq(CLIENT_USER_ID),
				businessStakeHolderModelsArgumentCaptor.capture());

		assertThat(result.getToken()).isEqualTo(STK_TOKEN);
		assertThat(result.getEmail()).isEqualTo(STK_EMAIL);
		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(businessStakeHolderModelsArgumentCaptor.getValue().stream().findFirst()
				.map(BusinessStakeHolderModel::getToken).orElse(StringUtils.EMPTY)).isEqualTo(STK_TOKEN);
	}

}
