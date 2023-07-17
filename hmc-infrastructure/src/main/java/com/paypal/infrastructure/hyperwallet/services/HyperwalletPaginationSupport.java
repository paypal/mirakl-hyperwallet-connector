package com.paypal.infrastructure.hyperwallet.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.*;
import com.hyperwallet.clientsdk.util.HyperwalletApiClient;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//@formatter:off
public class HyperwalletPaginationSupport {

	private final Hyperwallet hyperwallet;

	public HyperwalletPaginationSupport(final Hyperwallet hyperwallet) {
		this.hyperwallet = hyperwallet;
	}

	@NonNull
	public <T> List<T> get(final Supplier<HyperwalletList<T>> paginatedFunction) {
		HyperwalletList<T> page = paginatedFunction.get();
		final List<T> result = new ArrayList<>(page.getData());
		while (page.hasNextPage()) {
			final HyperwalletLink nextLink = page.getLinks().stream()
				.filter(x -> x.getParams().get("rel").equals("next"))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Could not find next page link"));
			page = followLink(nextLink, page);
			result.addAll(page.getData());
		}
		return result;
	}


	private <T> HyperwalletList<T> followLink(final HyperwalletLink link, final HyperwalletList<T> previousPage) {
		return getApiClient().get(link.getHref(), new HyperwalletListTypeReference<>(previousPage));
	}

	@SuppressWarnings({"java:S3011", "java:S2259"})
	protected HyperwalletApiClient getApiClient() {
		try {
			final Field field = ReflectionUtils.findField(Hyperwallet.class, "apiClient");
			assert field != null;
			field.setAccessible(true);
			return (HyperwalletApiClient) field.get(hyperwallet);
		}
		catch (final Exception e) {
			throw new IllegalStateException("Could not get apiClient field from Hyperwallet instance", e);
		}
	}

	private static final class HyperwalletListTypeReference<T> extends TypeReference<HyperwalletList<T>> {

		private final HyperwalletList<T> reference;

		public HyperwalletListTypeReference(final HyperwalletList<T> reference) {
			this.reference = reference;
		}

		@Override
		public Type getType() {
			return new HyperwalletListParametrizedType(reference);
		}

	}

	private static final class HyperwalletListParametrizedType implements ParameterizedType {

		private final Type[] actualTypeArguments;

		private final Type rawType;

		public HyperwalletListParametrizedType(final HyperwalletList<?> reference) {
			this.actualTypeArguments = new Type[]{reference.getData().get(0).getClass()};
			this.rawType = reference.getClass();
		}

		@Override
		public Type[] getActualTypeArguments() {
			return actualTypeArguments;
		}

		@Override
		public Type getRawType() {
			return rawType;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}
	}

}
