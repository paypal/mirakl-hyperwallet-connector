package com.paypal.infrastructure.hyperwallet.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletLink;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.util.HyperwalletApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletPaginationSupportTest {

	public static final int NUM_ITEMS = 10;

	public static final int PAGE_SIZE = 3;

	@InjectMocks
	@Spy
	private MyHyperwalletPaginationSupport testObj;

	@Mock
	private HyperwalletApiClient hyperwalletApiClientMock;

	@Mock
	private Supplier<HyperwalletList<Object>> paginatedFunctionMock;

	@Test
	void get_shouldReturnAllDataInEveryPage_whenThereAreMultiplePages() {
		// given
		when(paginatedFunctionMock.get()).thenReturn(hyperwalletList(NUM_ITEMS, PAGE_SIZE, 1));
		doReturn(hyperwalletApiClientMock).when(testObj).getApiClient();
		doReturn(hyperwalletList(NUM_ITEMS, PAGE_SIZE, 2)).when(hyperwalletApiClientMock).get(
				argThat(x -> x.contains("page=2")), ArgumentMatchers.<TypeReference<HyperwalletList<Object>>>any());
		doReturn(hyperwalletList(NUM_ITEMS, PAGE_SIZE, 3)).when(hyperwalletApiClientMock).get(
				argThat(x -> x.contains("page=3")), ArgumentMatchers.<TypeReference<HyperwalletList<Object>>>any());
		doReturn(hyperwalletList(NUM_ITEMS, PAGE_SIZE, 4)).when(hyperwalletApiClientMock).get(
				argThat(x -> x.contains("page=4")), ArgumentMatchers.<TypeReference<HyperwalletList<Object>>>any());

		// when
		final List<Object> result = testObj.get(paginatedFunctionMock);

		// then
		assertThat(result).hasSize(NUM_ITEMS);
	}

	@Test
	void get_shouldReturnAllData_whenThereAreOneFullPage() {
		// given
		when(paginatedFunctionMock.get()).thenReturn(hyperwalletList(PAGE_SIZE, PAGE_SIZE, 1));

		// when
		final List<Object> result = testObj.get(paginatedFunctionMock);

		// then
		assertThat(result).hasSize(PAGE_SIZE);
	}

	@Test
	void get_shouldReturnEmptyList_whenThereIsNoData() {
		// given
		when(paginatedFunctionMock.get()).thenReturn(hyperwalletList(0, PAGE_SIZE, 1));

		// when
		final List<Object> result = testObj.get(paginatedFunctionMock);

		// then
		assertThat(result).isEmpty();
	}

	HyperwalletList<Object> hyperwalletList(final int numItems, final int pageSize, final int numPage) {
		final int itemsInCurrentPage = numItems - pageSize * numPage >= 0 ? pageSize : numItems % pageSize;
		final boolean hasNextPage = numItems - pageSize * numPage > 0;
		final boolean hasPreviousPage = numPage == 0;
		final HyperwalletList<Object> hyperwalletList = new HyperwalletList<>();
		hyperwalletList.setLimit(PAGE_SIZE);
		hyperwalletList.setData(IntStream.range(0, itemsInCurrentPage).mapToObj(i -> mock(Object.class)).toList());
		hyperwalletList.setHasNextPage(hasNextPage);
		hyperwalletList.setHasPreviousPage(hasPreviousPage);

		final List<HyperwalletLink> links = new ArrayList<>();
		if (hasNextPage) {
			final HyperwalletLink nextLink = new HyperwalletLink();
			nextLink.setHref("http://localhost:8080/test?page=" + (numPage + 1));
			nextLink.setParams(Map.of("rel", "next"));
			links.add(nextLink);
		}
		if (hasPreviousPage) {
			final HyperwalletLink previousLink = new HyperwalletLink();
			previousLink.setHref("http://localhost:8080/test?page=" + (numPage - 1));
			previousLink.setParams(Map.of("rel", "previous"));
			links.add(previousLink);
		}
		hyperwalletList.setLinks(links);

		return hyperwalletList;
	}

	private static class MyHyperwalletPaginationSupport extends HyperwalletPaginationSupport {

		public MyHyperwalletPaginationSupport(final Hyperwallet hyperwallet) {
			super(hyperwallet);
		}

		@Override
		protected HyperwalletApiClient getApiClient() {
			return super.getApiClient();
		}

	}

}
