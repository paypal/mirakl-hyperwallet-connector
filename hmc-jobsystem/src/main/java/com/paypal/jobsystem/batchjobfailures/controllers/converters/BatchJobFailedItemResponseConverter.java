package com.paypal.jobsystem.batchjobfailures.controllers.converters;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.controllers.dto.BatchJobFailedItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchJobFailedItemResponseConverter {

	BatchJobFailedItemResponse toResponse(BatchJobFailedItem batchJobFailedItem);

	List<BatchJobFailedItemResponse> toResponse(List<BatchJobFailedItem> batchJobFailedItems);

}
