package com.paypal.infrastructure.impl;

import com.google.common.collect.ArrayListMultimap;
import com.paypal.infrastructure.BusinessStakeholderTestHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that helps with business stakeholder part in mockserver side
 */
@Service
@Profile({ "!prod" })
public class BusinessStakeholderTestHelperImpl implements BusinessStakeholderTestHelper {

	final ArrayListMultimap<String, List<String>> tokensMap = ArrayListMultimap.create();

	@Override
	public void storeRequiredVerificationBstk(final String bstkTokenList, String clientUserId) {

		final String[] requiredBstkList = bstkTokenList.split(",");
		this.tokensMap.put(clientUserId, Arrays.stream(requiredBstkList).collect(Collectors.toList()));
	}

	@Override
	public List<String> getRequiresVerificationBstk(String clientUserId) {

		//@formatter:off
		final List<String> tokens = tokensMap.get(clientUserId).stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		//@formatter:on

		tokensMap.removeAll(clientUserId);

		return tokens;
	}

}
