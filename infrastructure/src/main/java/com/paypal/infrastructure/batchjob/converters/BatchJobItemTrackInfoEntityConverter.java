package com.paypal.infrastructure.batchjob.converters;

import com.paypal.infrastructure.batchjob.dto.BatchJobItemTrackInfoResponse;
import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchJobItemTrackInfoEntityConverter {

	BatchJobItemTrackInfoResponse toResponse(BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity);

	List<BatchJobItemTrackInfoResponse> toResponse(List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities);

}
