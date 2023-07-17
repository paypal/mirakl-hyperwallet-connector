package com.paypal.infrastructure.itemlinks.repositories;

import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntity;
import com.paypal.infrastructure.itemlinks.entities.ItemLinkEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemLinkRepository extends JpaRepository<ItemLinkEntity, ItemLinkEntityId> {

	List<ItemLinkEntity> findBySourceSystemAndSourceIdAndSourceTypeAndTargetSystemAndTargetTypeIn(String sourceSystem,
			String sourceId, String sourceType, String targetSystem, Collection<String> targetTypes);

}
