package com.paypal.infrastructure.mirakl.settings;

public final class MiraklClientSettingsHolder {

	private MiraklClientSettingsHolder() {
		// private constructor
	}

	public static final MiraklClientSettings DEFAULT_SETTINGS = new MiraklClientSettings();

	private static final ThreadLocal<MiraklClientSettings> miraklClientSettingsThreadLocal = ThreadLocal
			.withInitial(() -> DEFAULT_SETTINGS);

	public static void setMiraklClientSettings(final MiraklClientSettings settings) {
		miraklClientSettingsThreadLocal.set(settings);
	}

	public static MiraklClientSettings getMiraklClientSettings() {
		return miraklClientSettingsThreadLocal.get();
	}

	public static void clear() {
		miraklClientSettingsThreadLocal.remove();
	}

}
