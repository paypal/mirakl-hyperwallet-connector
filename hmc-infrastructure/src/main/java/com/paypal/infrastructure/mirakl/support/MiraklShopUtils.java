package com.paypal.infrastructure.mirakl.support;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MiraklShopUtils {

	public static final String HYPERWALLET_BANK_ACCOUNT_TOKEN = "hw-bankaccount-token";

	public static final String HYPERWALLET_PROGRAM = "hw-program";

	private MiraklShopUtils() {
		// private constructor
	}

	public static Optional<String> getProgram(final MiraklShop miraklShop) {
		return getMiraklSingleValueListCustomFieldValue(miraklShop, HYPERWALLET_PROGRAM);
	}

	public static void setProgram(final MiraklShop miraklShop, final String program) {
		setMiraklSingleValueListCustomFieldValue(miraklShop, HYPERWALLET_PROGRAM, program);
	}

	public static Optional<String> getBankAccountToken(final MiraklShop miraklShop) {
		return getMiraklStringCustomFieldValue(miraklShop, HYPERWALLET_BANK_ACCOUNT_TOKEN);
	}

	public static void setBankAccountToken(final MiraklShop miraklShop, final String bankAccountToken) {
		setMiraklStringCustomFieldValue(miraklShop, HYPERWALLET_BANK_ACCOUNT_TOKEN, bankAccountToken);
	}

	public static void setMiraklSingleValueListCustomFieldValue(final MiraklShop miraklShop, final String code,
			final String value) {
		final Optional<MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue> existingFieldValue = getMiraklSingleValueListCustomField(
				miraklShop, code);
		existingFieldValue.ifPresentOrElse(f -> f.setValue(value),
				() -> addMiraklSingleValueListCustomFieldValue(miraklShop, code, value));
	}

	public static void setMiraklStringCustomFieldValue(final MiraklShop miraklShop, final String code,
			final String value) {
		final Optional<MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue> existingFieldValue = getMiraklStringCustomField(
				miraklShop, code);
		existingFieldValue.ifPresentOrElse(f -> f.setValue(value),
				() -> addMiraklStringCustomFieldValue(miraklShop, code, value));
	}

	private static void addMiraklSingleValueListCustomFieldValue(final MiraklShop miraklShop, final String code,
			final String value) {
		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue newFieldValue = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		newFieldValue.setCode(code);
		newFieldValue.setValue(value);
		addField(miraklShop, newFieldValue);
	}

	private static void addMiraklStringCustomFieldValue(final MiraklShop miraklShop, final String code,
			final String value) {
		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue newFieldValue = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		newFieldValue.setCode(code);
		newFieldValue.setValue(value);
		addField(miraklShop, newFieldValue);
	}

	private static void addField(final MiraklShop miraklShop, final MiraklAdditionalFieldValue newFieldValue) {
		final List<MiraklAdditionalFieldValue> additionalFieldValues = miraklShop.getAdditionalFieldValues();
		final List<MiraklAdditionalFieldValue> newAdditionalFieldValues = new ArrayList<>(
				additionalFieldValues != null ? additionalFieldValues : List.of());
		newAdditionalFieldValues.add(newFieldValue);
		miraklShop.setAdditionalFieldValues(newAdditionalFieldValues);
	}

	public static Optional<MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue> getMiraklStringCustomField(
			final @NotNull MiraklShop shop, final String customFieldCode) {
		return shop.getAdditionalFieldValues() != null
				? getMiraklStringCustomField(shop.getAdditionalFieldValues(), customFieldCode) : Optional.empty();
	}

	public static Optional<MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue> getMiraklSingleValueListCustomField(
			final @NotNull MiraklShop shop, final String customFieldCode) {
		return shop.getAdditionalFieldValues() != null
				? getMiraklSingleValueListCustomField(shop.getAdditionalFieldValues(), customFieldCode)
				: Optional.empty();
	}

	public static Optional<String> getMiraklStringCustomFieldValue(final @NotNull MiraklShop shop,
			final String customFieldCode) {
		return shop.getAdditionalFieldValues() != null
				? getMiraklStringCustomFieldValue(shop.getAdditionalFieldValues(), customFieldCode) : Optional.empty();
	}

	public static Optional<String> getMiraklSingleValueListCustomFieldValue(final @NotNull MiraklShop shop,
			final String customFieldCode) {
		return shop.getAdditionalFieldValues() != null
				? getMiraklSingleValueListCustomFieldValue(shop.getAdditionalFieldValues(), customFieldCode)
				: Optional.empty();
	}

	private static Optional<String> getMiraklStringCustomFieldValue(final List<MiraklAdditionalFieldValue> fields,
			final String customFieldCode) {
		//@formatter:off
		return getMiraklStringCustomField(fields, customFieldCode)
				.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
		//@formatter:on
	}

	private static Optional<MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue> getMiraklStringCustomField(
			final List<MiraklAdditionalFieldValue> fields, final String customFieldCode) {
		//@formatter:off
		return fields.stream().filter(field -> field.getCode().equals(customFieldCode))
				.filter(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::isInstance)
				.map(MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue.class::cast)
				.findAny();
		//@formatter:on
	}

	private static Optional<String> getMiraklSingleValueListCustomFieldValue(
			final List<MiraklAdditionalFieldValue> fields, final String customFieldCode) {
		//@formatter:off
		return getMiraklSingleValueListCustomField(fields, customFieldCode)
				.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue);
		//@formatter:on
	}

	private static Optional<MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue> getMiraklSingleValueListCustomField(
			final List<MiraklAdditionalFieldValue> fields, final String customFieldCode) {
		//@formatter:off
		return fields.stream()
				.filter(field -> field.getCode().equals(customFieldCode))
				.filter(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::isInstance)
				.map(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::cast)
				.findAny();
		//@formatter:on
	}

}
