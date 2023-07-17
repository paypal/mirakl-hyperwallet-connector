package com.paypal.sellers.sellerextractioncommons.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletUsersListPaginationOptions;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellersTokenSynchronizationServiceImplTest {

	public static final String USR_TOKEN = "usr-1291289283";

	@InjectMocks
	private SellersTokenSynchronizationServiceImpl testObj;

	@Mock
	private UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	private SellerModel sellerModelStub;

	private HyperwalletUser hyperwalletUser;

	@BeforeEach
	void setUp() {
		hyperwalletUser = new HyperwalletUser();
		hyperwalletUser.setToken(USR_TOKEN);
	}

	@Test
	void synchronizeToken_shouldDoNothingAndReturnTheSellerWhenTokenExists() {
		createSellerWithTokenAndClientUserId("prg-12312222", "usr-1291289283", "2000");

		final SellerModel result = testObj.synchronizeToken(sellerModelStub);

		assertThat(result).isEqualTo(sellerModelStub);
	}

	@Test
	void synchronizeToken_shouldReturnSameSellerWhenSellerDoesNotExistsOnHyperwallet() {
		createSellerWithTokenAndClientUserId("prg-12312222", null, "2000");
		prepareHyperwalletSDKInstance(sellerModelStub);
		hyperwalletAPIReturnsAnEmptyListForClientUserId("2000");

		final SellerModel result = testObj.synchronizeToken(sellerModelStub);

		assertThat(result).isEqualTo(sellerModelStub);
	}

	@Test
	void synchronizeToken_shouldReturnTheSellerWithTheTokenRetrievedFromHyperwalletWhenSellerExistsOnHyperwalletAndTheTokenDoesNotExistOnTheSeller() {
		createSellerWithTokenAndClientUserId("prg-12312222", null, "2000");
		prepareHyperwalletSDKInstance(sellerModelStub);
		hyperwalletAPIReturnsAListWithOneUserForClientUserId("2000");

		final SellerModel result = testObj.synchronizeToken(sellerModelStub);

		assertThat(result.getToken()).isEqualTo(USR_TOKEN);
	}

	@Test
	void synchronizeToken_shouldThrowAnHMCHyperwalletAPIExceptionWhenHyperwalletSDKReturnsAnHyperwalletException() {
		createSellerWithTokenAndClientUserId("prg-12312222", null, "2000");
		prepareHyperwalletSDKInstance(sellerModelStub);
		hyperwalletSDKThrowsAnException();

		assertThatThrownBy(() -> testObj.synchronizeToken(sellerModelStub))
				.isInstanceOf(HMCHyperwalletAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Hyperwallet API");
	}

	@Test
	void synchronizeToken_shouldSetOnMiraklTheHyperwalletTokenWhenSellersExistsOnHyperwalletAndTheTokenDoesNotExistsOnTheSeller() {
		createSellerWithTokenAndClientUserId("prg-12312222", null, "2000");
		prepareHyperwalletSDKInstance(sellerModelStub);
		hyperwalletAPIReturnsAListWithOneUserForClientUserId("2000");

		testObj.synchronizeToken(sellerModelStub);

		verify(miraklSellersExtractServiceMock).updateUserToken(hyperwalletUser);
	}

	@Test
	void synchronizeToken_shouldThrowAnHMCMiraklApiExceptionWhenMiraklReturnsAnMiraklException() {
		createSellerWithTokenAndClientUserId("prg-12312222", null, "2000");
		prepareHyperwalletSDKInstance(sellerModelStub);
		hyperwalletAPIReturnsAListWithOneUserForClientUserId("2000");
		miraklSDKThrowsAnException();

		assertThatThrownBy(() -> testObj.synchronizeToken(sellerModelStub)).isInstanceOf(HMCMiraklAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Mirakl API");
	}

	@NotNull
	private void createSellerWithTokenAndClientUserId(final String programToken, final String userToken,
			final String clientUserId) {
		sellerModelStub = SellerModel.builder().programToken(programToken).token(userToken).clientUserId(clientUserId)
				.build();
	}

	private void hyperwalletAPIReturnsAnEmptyListForClientUserId(final String clientUserId) {
		final HyperwalletUsersListPaginationOptions options = new HyperwalletUsersListPaginationOptions();
		options.setClientUserId(clientUserId);
		when(hyperwalletMock.listUsers(argThat(new HyperwalletUsersListPaginationOptionsMatcher(options))))
				.thenReturn(new HyperwalletList<>());
	}

	private void hyperwalletAPIReturnsAListWithOneUserForClientUserId(final String clientUserId) {
		final HyperwalletUsersListPaginationOptions options = new HyperwalletUsersListPaginationOptions();
		options.setClientUserId(clientUserId);

		final HyperwalletList<HyperwalletUser> userHyperwalletList = new HyperwalletList<>();
		userHyperwalletList.setData(List.of(hyperwalletUser));
		when(hyperwalletMock.listUsers(argThat(new HyperwalletUsersListPaginationOptionsMatcher(options))))
				.thenReturn(userHyperwalletList);
	}

	private void hyperwalletSDKThrowsAnException() {
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletMock)
				.listUsers(any(HyperwalletUsersListPaginationOptions.class));

	}

	private void miraklSDKThrowsAnException() {
		doThrow(new MiraklException("Something went wrong")).when(miraklSellersExtractServiceMock)
				.updateUserToken(any(HyperwalletUser.class));

	}

	private void prepareHyperwalletSDKInstance(final SellerModel sellerModel) {
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(sellerModel.getProgramToken()))
				.thenReturn(hyperwalletMock);
	}

	public class HyperwalletUsersListPaginationOptionsMatcher
			implements ArgumentMatcher<HyperwalletUsersListPaginationOptions> {

		private final HyperwalletUsersListPaginationOptions left;

		public HyperwalletUsersListPaginationOptionsMatcher(final HyperwalletUsersListPaginationOptions options) {
			this.left = options;
		}

		@Override
		public boolean matches(final HyperwalletUsersListPaginationOptions right) {
			return Objects.equals(left.getClientUserId(), right.getClientUserId())
					&& Objects.equals(left.getProgramToken(), right.getProgramToken())
					&& Objects.equals(left.getEmail(), right.getEmail())
					&& Objects.equals(left.getStatus(), right.getStatus())
					&& Objects.equals(left.getVerificationStatus(), right.getVerificationStatus());
		}

	}

}
