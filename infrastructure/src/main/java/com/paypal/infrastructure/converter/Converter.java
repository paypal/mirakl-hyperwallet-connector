package com.paypal.infrastructure.converter;

import org.springframework.lang.NonNull;

/**
 * Interface to convert objects from source class {@link S} into target class {@link T}
 *
 * @param <S> Source class
 * @param <T> Target class
 */
public interface Converter<S, T> {

	/**
	 * Method that retrieves a {@link S} and returns a {@link T}
	 * @param source the source object {@link S}
	 * @return the returned object {@link T}
	 */
	T convert(@NonNull final S source);

}
