package com.paypal.jobsystem.quartzintegration.repositories;

import com.paypal.jobsystem.quartzintegration.repositories.entities.JobExecutionInformationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link JobExecutionInformationEntity}
 */
@Repository
@Transactional
public interface JobExecutionInformationRepository extends CrudRepository<JobExecutionInformationEntity, Long> {

	JobExecutionInformationEntity findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(String jobType);

}
