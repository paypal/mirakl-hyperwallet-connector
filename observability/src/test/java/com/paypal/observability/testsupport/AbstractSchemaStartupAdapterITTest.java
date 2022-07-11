package com.paypal.observability.testsupport;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractSchemaStartupAdapterITTest extends AbstractMockServerITTest {

	protected void assertThatContainsMessage(final String[] report, final String error) {
		assertThat(Arrays.stream(report).filter(x -> matchErrorMessage(error, x)).findAny()).isPresent();
	}

	private boolean matchErrorMessage(final String error, final String reportMessage) {
		return reportMessage.contains(error) && reportMessage.contains("\nExpected value:")
				&& reportMessage.contains("\nActual value:");
	}

	protected void assertThatContainsSetMessage(final String[] report, final String error) {
		assertThat(Arrays.stream(report).filter(x -> matchSetErrorMessage(error, x)).findAny()).isPresent();
	}

	private boolean matchSetErrorMessage(final String error, final String reportMessage) {
		return reportMessage.contains(error) && reportMessage.contains("\nOffending field details:");
	}

}
