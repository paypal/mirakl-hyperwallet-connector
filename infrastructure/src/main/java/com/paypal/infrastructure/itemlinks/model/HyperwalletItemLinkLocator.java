package com.paypal.infrastructure.itemlinks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a reference to a Hyperwallet item.
 */
@Data
@Builder
@AllArgsConstructor
public class HyperwalletItemLinkLocator implements ItemLinkLocator<HyperwalletItemTypes> {

	private String id;

	private HyperwalletItemTypes type;

	@Override
	public ItemLinkExternalSystem getSystem() {
		return ItemLinkExternalSystem.HYPERWALLET;
	}

}
