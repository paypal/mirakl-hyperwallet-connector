package com.paypal.infrastructure;

import java.util.List;

public interface BusinessStakeholderTestHelper {

	void storeRequiredVerificationBstk(final String bstkTokenList, String clientUserId);

	List<String> getRequiresVerificationBstk(String clientUserId);

}
