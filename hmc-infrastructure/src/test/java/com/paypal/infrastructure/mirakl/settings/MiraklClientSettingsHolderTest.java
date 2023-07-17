package com.paypal.infrastructure.mirakl.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklClientSettingsHolderTest {

	private MiraklClientSettings miraklClientSettings;

	@BeforeEach
	void setUp() {
		miraklClientSettings = new MiraklClientSettings(true);
	}

	@AfterEach
	void tearDown() {
		MiraklClientSettingsHolder.clear();
	}

	@Test
	void setMiraklClientSettings_shouldUpdateTheSettings_whenExecuted() {
		MiraklClientSettingsHolder.setMiraklClientSettings(miraklClientSettings);
		assertThat(MiraklClientSettingsHolder.getMiraklClientSettings()).isEqualTo(miraklClientSettings);
	}

	@Test
	void getMiraklClientSettings_shouldReturnTheSettings_whenExecuted() {
		assertThat(MiraklClientSettingsHolder.getMiraklClientSettings())
				.isEqualTo(MiraklClientSettingsHolder.DEFAULT_SETTINGS);
	}

	@Test
	void clear_shouldClearSettings_whenExecuted() {
		MiraklClientSettingsHolder.setMiraklClientSettings(miraklClientSettings);
		MiraklClientSettingsHolder.clear();
		assertThat(MiraklClientSettingsHolder.getMiraklClientSettings())
				.isEqualTo(MiraklClientSettingsHolder.DEFAULT_SETTINGS);
	}

}
