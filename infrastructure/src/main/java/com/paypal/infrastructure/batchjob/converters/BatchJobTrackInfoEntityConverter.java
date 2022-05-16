package com.paypal.infrastructure.batchjob.converters;

import com.paypal.infrastructure.batchjob.dto.BatchJobTrackInfoResponse;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchJobTrackInfoEntityConverter {

	BatchJobTrackInfoResponse toResponse(BatchJobTrackInfoEntity batchJobTrackInfoEntity);

	List<BatchJobTrackInfoResponse> toResponse(List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities);

}
