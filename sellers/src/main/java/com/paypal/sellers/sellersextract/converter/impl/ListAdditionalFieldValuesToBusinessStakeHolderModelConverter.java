package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Converst from a pair of a list of {@link MiraklAdditionalFieldValue} and
 * {@link Integer} into {@link BusinessStakeHolderModel}
 */
@Service
public class ListAdditionalFieldValuesToBusinessStakeHolderModelConverter
		implements Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> {

	/**
	 * Method that retrieves a {@link Pair<List<MiraklAdditionalFieldValue>, Integer>} and
	 * returns a {@link BusinessStakeHolderModel}
	 * @param source the source object {@link Pair<List<MiraklAdditionalFieldValue>,
	 * Integer>}
	 * @return the returned object {@link BusinessStakeHolderModel}
	 */
	@Override
	public BusinessStakeHolderModel convert(final Triple<List<MiraklAdditionalFieldValue>, Integer, String> source) {

		final List<MiraklAdditionalFieldValue> additionalFieldValues = source.getLeft();
		final Integer businessStakeHolderNumber = source.getMiddle();
		final String clientId = source.getRight();
		//@formatter:off
        return getBuilder()
				.stkId(businessStakeHolderNumber)
				.token(additionalFieldValues, businessStakeHolderNumber)
				.userToken(additionalFieldValues)
				.clientUserId(clientId)
				.businessContact(additionalFieldValues, businessStakeHolderNumber)
				.director(additionalFieldValues, businessStakeHolderNumber)
				.ubo(additionalFieldValues, businessStakeHolderNumber)
				.smo(additionalFieldValues, businessStakeHolderNumber)
				.firstName(additionalFieldValues, businessStakeHolderNumber)
				.middleName(additionalFieldValues, businessStakeHolderNumber)
				.lastName(additionalFieldValues, businessStakeHolderNumber)
				.dateOfBirth(additionalFieldValues, businessStakeHolderNumber)
				.countryOfBirth(additionalFieldValues, businessStakeHolderNumber)
				.countryOfNationality(additionalFieldValues, businessStakeHolderNumber)
				.gender(additionalFieldValues, businessStakeHolderNumber)
				.phoneNumber(additionalFieldValues, businessStakeHolderNumber)
				.mobileNumber(additionalFieldValues, businessStakeHolderNumber)
				.email(additionalFieldValues, businessStakeHolderNumber)
				.governmentId(additionalFieldValues, businessStakeHolderNumber)
				.governmentIdType(additionalFieldValues, businessStakeHolderNumber)
				.driversLicenseId(additionalFieldValues, businessStakeHolderNumber)
				.addressLine1(additionalFieldValues, businessStakeHolderNumber)
				.addressLine2(additionalFieldValues, businessStakeHolderNumber)
				.city(additionalFieldValues, businessStakeHolderNumber)
				.stateProvince(additionalFieldValues, businessStakeHolderNumber)
				.country(additionalFieldValues, businessStakeHolderNumber)
				.postalCode(additionalFieldValues, businessStakeHolderNumber)
				.hyperwalletProgram(additionalFieldValues)
                .build();
        //@formatter:on
	}

	protected BusinessStakeHolderModel.BusinessStakeHolderModelBuilder getBuilder() {
		return BusinessStakeHolderModel.builder();
	}

}
