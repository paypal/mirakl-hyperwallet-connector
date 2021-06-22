package com.paypal.kyc.strategies.documents.flags;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractUserDocumentFlagsStrategyTest {

	private MyAbstractUserDocumentFlagsStrategy testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private MailNotificationUtil mailNotificationMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestArgumentCaptor;

	private static final String SHOP_ID = "2000";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	@BeforeEach
	void setUp() {
		testObj = new MyAbstractUserDocumentFlagsStrategy(mailNotificationMock,
				miraklMarketplacePlatformOperatorApiClientMock);
	}

	@Test
	void fillMiraklProofIdentityOrBusinessFlagStatus_shouldSetFlagProofIdentificationToTrueInMirakl() {
		//@formatter:off
        final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
                .builder()
                .clientUserId(SHOP_ID)
                .profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
                .verificationStatus(HyperwalletUser.VerificationStatus.REQUIRED).build();
        //@formatter:on
		testObj.fillMiraklProofIdentityOrBusinessFlagStatus(kycUserDocumentFlagsNotificationBodyModel);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(miraklUpdateShopsRequestArgumentCaptor.capture());

		final MiraklUpdateShopsRequest updateShopRequest = miraklUpdateShopsRequestArgumentCaptor.getValue();

		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue additionalValue = new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
				HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD, "true");

		assertThat(updateShopRequest.getShops()).hasSize(1);
		final MiraklUpdateShop shop = updateShopRequest.getShops().get(0);
		assertThat(shop.getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = shop.getAdditionalFieldValues();
		assertThat(additionalFieldValues).hasSize(1);
		final MiraklRequestAdditionalFieldValue additionalFieldValue = additionalFieldValues.get(0);
		assertThat(additionalFieldValue.getCode()).isEqualTo(HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
		assertThat(additionalFieldValue)
				.isInstanceOf(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class);
		assertThat(((MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue)
				.getValue()).isEqualTo(Boolean.TRUE.toString());
	}

	@Test
	void fillMiraklProofIdentityOrBusinessFlagStatus_shouldSendAnEmailWhenMiraklConnectionFails() {
		//@formatter:off
        final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
                .builder()
                .clientUserId(SHOP_ID)
                .profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
                .verificationStatus(HyperwalletUser.VerificationStatus.REQUIRED).build();

        final MiraklException miraklException = new MiraklException("Something bad happened");

        doThrow(miraklException).when(miraklMarketplacePlatformOperatorApiClientMock).updateShops(any(MiraklUpdateShopsRequest.class));
        //@formatter:on
		testObj.fillMiraklProofIdentityOrBusinessFlagStatus(kycUserDocumentFlagsNotificationBodyModel);

		verify(mailNotificationMock).sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
				String.format(ERROR_MESSAGE_PREFIX + "Something went wrong updating KYC information of shop [%s]%n%s",
						SHOP_ID, MiraklLoggingErrorsUtil.stringify(miraklException)));
	}

	private static class MyAbstractUserDocumentFlagsStrategy extends AbstractUserDocumentFlagsStrategy {

		private MyAbstractUserDocumentFlagsStrategy(final MailNotificationUtil mailNotificationUtil,
				final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient) {
			super(mailNotificationUtil, miraklMarketplacePlatformOperatorApiClient);
		}

	}

}
