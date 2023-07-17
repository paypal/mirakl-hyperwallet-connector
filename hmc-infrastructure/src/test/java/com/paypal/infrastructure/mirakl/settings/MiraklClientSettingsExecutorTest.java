package com.paypal.infrastructure.mirakl.settings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MiraklClientSettingsExecutorTest {

	private MiraklClientSettings miraklClientSettings;

	private Runnable executorTestRunnable;

	@Test
	void runWithSettings_shouldSetTheSettingsToRunnableAndFinallyClearThem_afterBeingExecutedWithStagedChanges() {
		// given
		miraklClientSettings = new MiraklClientSettings(true);
		executorTestRunnable = () -> {
			final MiraklClientSettings miraklClientSettingsIntoRunnable = MiraklClientSettingsHolder
					.getMiraklClientSettings();
			assertThat(miraklClientSettingsIntoRunnable).isEqualTo(miraklClientSettings);
		};

		// when
		MiraklClientSettingsExecutor.runWithSettings(miraklClientSettings, executorTestRunnable);

		// then
		assertThat(MiraklClientSettingsHolder.getMiraklClientSettings())
				.isEqualTo(MiraklClientSettingsHolder.DEFAULT_SETTINGS);
	}

	@Test
	void runWithSettings_shouldClearTheSettings_IfAnExceptionIsTriggered() {
		// given
		miraklClientSettings = new MiraklClientSettings(true);
		executorTestRunnable = () -> {
			throw new IllegalArgumentException("This is an Exception.");
		};

		// when
		assertThatThrownBy(
				() -> MiraklClientSettingsExecutor.runWithSettings(miraklClientSettings, executorTestRunnable))
						.isInstanceOf(IllegalArgumentException.class);

		// then
		assertThat(MiraklClientSettingsHolder.getMiraklClientSettings())
				.isEqualTo(MiraklClientSettingsHolder.DEFAULT_SETTINGS);
	}

}
