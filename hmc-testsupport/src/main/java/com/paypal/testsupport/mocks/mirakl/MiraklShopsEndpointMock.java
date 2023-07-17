package com.paypal.testsupport.mocks.mirakl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShops;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShopReturn;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Parameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

public class MiraklShopsEndpointMock extends AbstractResourceLoadingEndpointMock {

	private static final String URL = "/api/shops";

	private static final String MOCKS_FOLDER = "mocks/mirakl/shops";

	public MiraklShopsEndpointMock(final MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	public void getShops(final Date updatedSince, final boolean paginate, final String responseFile) {
		final String jsonReturned = loadResource(responseFile);

		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod(HttpMethod.GET.name())
						.withPath(URL)
						.withQueryStringParameters(
								new Parameter("updated_since", updatedSince.toInstant().toString()),
								new Parameter("paginate", String.valueOf(paginate))))
					.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(jsonReturned));
		//@formatter:on
	}

	public void updateDocument(final String shopId) throws JsonProcessingException {
		final MiraklUpdatedShops dtoReturned = createUpdateShopReturn(shopId);
		final MiraklUpdateShopsRequest dtoRequested = createUpdateShopRequest(Long.parseLong(shopId));

		final String jsonReturned = mapper.writeValueAsString(dtoReturned);
		final String jsonRequested = mapper.writeValueAsString(new MiraklUpdateShops(dtoRequested.getShops()));
		mockServerClient.when(request().withMethod(HttpMethod.PUT.name()).withPath(URL).withBody(jsonRequested))
				.respond(response().withStatusCode(HttpStatus.OK.value()).withBody(jsonReturned));
	}

	public void verifyUpdateDocument(final Long shopId) throws JsonProcessingException {
		final MiraklUpdateShopsRequest expected = createUpdateShopRequest(shopId);
		final String jsonRequested = mapper.writeValueAsString(new MiraklUpdateShops(expected.getShops()));

		mockServerClient.verify(request().withMethod(HttpMethod.PUT.name()).withPath(URL).withBody(jsonRequested),
				exactly(1));
	}

	private static MiraklUpdateShopsRequest createUpdateShopRequest(final long shopId) {
		final MiraklUpdateShop shop = new MiraklUpdateShop();
		shop.setShopId(shopId);
		shop.setAdditionalFieldValues(
				List.of(new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
						"hw-kyc-req-proof-authorization", "false")));

		return new MiraklUpdateShopsRequest(List.of(shop));
	}

	private static MiraklUpdatedShops createUpdateShopReturn(final String shopId) {
		final MiraklShop shop = new MiraklShop();
		shop.setId(shopId);

		final MiraklUpdatedShopReturn returnWrapper = new MiraklUpdatedShopReturn();
		returnWrapper.setShopUpdated(shop);
		final MiraklUpdatedShops value = new MiraklUpdatedShops();
		value.setShopReturns(List.of(returnWrapper));
		return value;
	}

	@Override
	protected String getFolder() {
		return MOCKS_FOLDER;
	}

}
