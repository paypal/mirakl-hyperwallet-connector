package com.paypal.infrastructure.itemlinks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a reference to a Mirakl item.
 */
@Data
@Builder
@AllArgsConstructor
public class MiraklItemLinkLocator implements ItemLinkLocator<MiraklItemTypes> {

	private String id;

	private MiraklItemTypes type;

	@Override
	public ItemLinkExternalSystem getSystem() {
		return ItemLinkExternalSystem.MIRAKL;
	}

}
