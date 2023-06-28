package com.paypal.infrastructure.changestaging.repositories;

import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StagedChangesRepository extends JpaRepository<StagedChangeEntity, String> {

	List<StagedChangeEntity> findByTypeAndOperationAndTargetOrderByCreationDateAsc(String type,
			ChangeOperation operation, ChangeTarget target, Pageable pageable);

	void deleteByIdIn(List<String> ids);

}
