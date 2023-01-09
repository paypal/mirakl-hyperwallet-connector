package com.paypal.infrastructure.test.mocks.mirakl;

import org.mockserver.client.MockServerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.mockserver.model.BinaryBody.binary;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class MiraklShopsDocumentsEndpointMock extends AbstractResourceLoadingEndpointMock {

	private static final String URL = "/api/shops/documents";

	private static final String URL_DOWNLOAD = "/api/shops/documents/download";

	public static final String MOCKS_FOLDER = "mocks/mirakl/shops/documents";

	public MiraklShopsDocumentsEndpointMock(MockServerClient mockServerClient) {
		super(mockServerClient);
	}

	public void getShopDocuments(String shopId, String responseFile) {
		final String responseFileContent = loadResource(responseFile);

		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod(HttpMethod.GET.name())
						.withPath(URL)
						.withQueryStringParameter("shop_ids", shopId))
				.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(responseFileContent));
		//@formatter:on
	}

	public void getShopDocument(String documentId, String responseFile) {
		final byte[] responseFileContent = loadResourceAsBinary(responseFile);

		//@formatter:off
		mockServerClient
				.when(request()
						.withMethod(HttpMethod.GET.name())
						.withPath(URL_DOWNLOAD)
						.withQueryStringParameter("document_ids", documentId))
				.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withHeaders(
								header(CONTENT_TYPE, MediaType.IMAGE_PNG.toString()),
								header(CONTENT_DISPOSITION, "attachment; filename=\"" + responseFile + "\"")
						)
						.withBody(binary(responseFileContent)));
		//@formatter:on
	}

	@Override
	protected String getFolder() {
		return MOCKS_FOLDER;
	}

}
