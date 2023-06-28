package com.paypal.infrastructure.itemlinks.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemLinkEntityId implements Serializable {

	private String sourceId;

	private String sourceType;

	private String sourceSystem;

	private String targetType;

	private String targetSystem;

}
