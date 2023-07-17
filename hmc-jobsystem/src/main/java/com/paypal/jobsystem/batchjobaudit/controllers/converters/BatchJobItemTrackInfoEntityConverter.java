package com.paypal.jobsystem.batchjobaudit.controllers.converters;

import com.paypal.jobsystem.batchjobaudit.controllers.dto.BatchJobItemTrackInfoResponse;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchJobItemTrackInfoEntityConverter {

	BatchJobItemTrackInfoResponse toResponse(BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity);

	List<BatchJobItemTrackInfoResponse> toResponse(List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities);

}
