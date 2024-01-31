package com.paypal.infrastructure.mirakl.client.filter;

import com.mirakl.client.mmp.domain.shop.MiraklShops;

public interface ShopsFilter {

	void filterShops(final MiraklShops shops);

}
