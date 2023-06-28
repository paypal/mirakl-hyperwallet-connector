package com.paypal.infrastructure.support.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Controller advice for handling HMC exceptions.
 */
@Slf4j
@RestControllerAdvice
public class HMCExceptionHandler {

	/**
	 * Handles {@link HMCException} exceptions.
	 * @param exception a {@link HMCException} exception.
	 * @return a {@link ResponseEntity} of {@link HMCErrorResponse} with http bad request
	 * status and an exception error message.
	 */
	@ExceptionHandler(HMCException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public HMCErrorResponse handleHMCException(final HMCException exception) {

		log.warn(exception.getMessage(), exception);

		return new HMCErrorResponse(exception.getMessage());
	}

	/**
	 * Handles {@link MethodArgumentTypeMismatchException} exceptions.
	 * @param exception a {@link MethodArgumentTypeMismatchException} exception.
	 * @return a {@link ResponseEntity} of {@link HMCErrorResponse} with http bad request
	 * status and an exception error message.
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public HMCErrorResponse handleMethodArgumentTypeMismatchException(
			final MethodArgumentTypeMismatchException exception) {

		log.warn(exception.getMessage(), exception);

		return new HMCErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase());
	}

	/**
	 * Handles {@link MissingServletRequestParameterException} exceptions.
	 * @param exception a {@link MissingServletRequestParameterException} exception.
	 * @return a {@link ResponseEntity} of {@link HMCErrorResponse} with http bad request
	 * status and an exception error message.
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public HMCErrorResponse handleMissingRequestParametersException(
			final MissingServletRequestParameterException exception) {

		log.warn(exception.getMessage(), exception);

		return new HMCErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase());
	}

}
