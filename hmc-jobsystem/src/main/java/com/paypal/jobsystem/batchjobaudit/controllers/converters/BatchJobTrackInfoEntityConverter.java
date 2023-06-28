package com.paypal.jobsystem.batchjobaudit.controllers.converters;

import com.paypal.jobsystem.batchjobaudit.controllers.dto.BatchJobTrackInfoResponse;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchJobTrackInfoEntityConverter {

	BatchJobTrackInfoResponse toResponse(BatchJobTrackInfoEntity batchJobTrackInfoEntity);

	List<BatchJobTrackInfoResponse> toResponse(List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities);

}
