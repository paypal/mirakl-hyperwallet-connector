package com.paypal.infrastructure.mirakl.settings;

public final class MiraklClientSettingsExecutor {

	private MiraklClientSettingsExecutor() {
		// private constructor
	}

	public static void runWithSettings(final MiraklClientSettings settings, final Runnable runnable) {
		MiraklClientSettingsHolder.setMiraklClientSettings(settings);
		try {
			runnable.run();
		}
		finally {
			MiraklClientSettingsHolder.clear();
		}
	}

}
