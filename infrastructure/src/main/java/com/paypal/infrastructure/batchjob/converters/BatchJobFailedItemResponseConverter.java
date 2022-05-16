package com.paypal.infrastructure.batchjob.converters;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import com.paypal.infrastructure.batchjob.dto.BatchJobFailedItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchJobFailedItemResponseConverter {

	BatchJobFailedItemResponse toResponse(BatchJobFailedItem batchJobFailedItem);

	List<BatchJobFailedItemResponse> toResponse(List<BatchJobFailedItem> batchJobFailedItems);

}
