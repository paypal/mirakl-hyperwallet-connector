package com.paypal.sellers.batchjobs.bstk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersRetryBatchJobItemsExtractorTest {

	@InjectMocks
	private BusinessStakeholdersRetryBatchJobItemsExtractor testObj;

	@Test
	void getItem_shouldReturnBusinessStakeholderType() {
		String result = testObj.getItemType();

		assertThat(result).isEqualTo(BusinessStakeholderExtractJobItem.ITEM_TYPE);
	}

}
