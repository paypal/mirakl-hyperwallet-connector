package com.paypal.infrastructure.batchjob.controller;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.converters.BatchJobFailedItemResponseConverter;
import com.paypal.infrastructure.batchjob.dto.BatchJobFailedItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobFailuresControllerTest {

	@InjectMocks
	private JobFailuresController testObj;

	@Mock
	private BatchJobFailedItemService batchJobFailedItemServiceMock;

	@Mock
	private BatchJobFailedItemResponseConverter batchJobFailedItemResponseConverterMock;

	@Mock
	private BatchJobFailedItem batchJobFailedItem1Mock, batchJobFailedItem2Mock;

	@Mock
	private BatchJobFailedItemResponse batchJobFailedItemResponse1Mock, batchJobFailedItemResponse2Mock;

	@Test
	void getFailedItems_ShouldReturnAllFailedItemsOfAType() {
		List<BatchJobFailedItem> batchJobFailedItems = List.of(batchJobFailedItem1Mock, batchJobFailedItem2Mock);
		List<BatchJobFailedItemResponse> batchJobFailedItemResponses = List.of(batchJobFailedItemResponse1Mock,
				batchJobFailedItemResponse2Mock);

		when(batchJobFailedItemServiceMock.getFailedItems("type1")).thenReturn(batchJobFailedItems);
		when(batchJobFailedItemResponseConverterMock.toResponse(batchJobFailedItems))
				.thenReturn(batchJobFailedItemResponses);

		List<BatchJobFailedItemResponse> result = testObj.getFailedItems("type1");

		assertThat(result).containsAll(batchJobFailedItemResponses);
	}

}
