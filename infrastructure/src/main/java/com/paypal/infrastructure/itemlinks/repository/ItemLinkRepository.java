package com.paypal.infrastructure.itemlinks.repository;

import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemLinkRepository extends JpaRepository<ItemLinkEntity, ItemLinkEntity> {

	List<ItemLinkEntity> findBySourceSystemAndSourceIdAndSourceTypeAndTargetSystemAndTargetTypeIn(String sourceSystem,
			String sourceId, String sourceType, String targetSystem, Collection<String> targetTypes);

}
