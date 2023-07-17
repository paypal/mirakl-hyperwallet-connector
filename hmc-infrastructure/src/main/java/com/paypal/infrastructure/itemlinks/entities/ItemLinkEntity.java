package com.paypal.infrastructure.itemlinks.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ItemLinkEntityId.class)
@Table(indexes = { @Index(columnList = "sourceId,sourceType,sourceSystem,targetSystem,targetType") })
public class ItemLinkEntity implements Serializable {

	@Id
	private String sourceId;

	@Id
	private String sourceType;

	@Id
	private String sourceSystem;

	private String targetId;

	@Id
	private String targetType;

	@Id
	private String targetSystem;

}
