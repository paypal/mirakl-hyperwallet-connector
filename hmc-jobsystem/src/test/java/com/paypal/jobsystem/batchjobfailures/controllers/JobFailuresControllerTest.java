package com.paypal.jobsystem.batchjobfailures.controllers;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.controllers.converters.BatchJobFailedItemResponseConverter;
import com.paypal.jobsystem.batchjobfailures.controllers.dto.BatchJobFailedItemResponse;
import com.paypal.jobsystem.batchjobfailures.controllers.JobFailuresController;
import org.assertj.core.api.Assertions;
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
		final List<BatchJobFailedItem> batchJobFailedItems = List.of(batchJobFailedItem1Mock, batchJobFailedItem2Mock);
		final List<BatchJobFailedItemResponse> batchJobFailedItemResponses = List.of(batchJobFailedItemResponse1Mock,
				batchJobFailedItemResponse2Mock);

		when(batchJobFailedItemServiceMock.getFailedItems("type1")).thenReturn(batchJobFailedItems);
		when(batchJobFailedItemResponseConverterMock.toResponse(batchJobFailedItems))
				.thenReturn(batchJobFailedItemResponses);

		final List<BatchJobFailedItemResponse> result = testObj.getFailedItems("type1");

		assertThat(result).containsAll(batchJobFailedItemResponses);
	}

}
