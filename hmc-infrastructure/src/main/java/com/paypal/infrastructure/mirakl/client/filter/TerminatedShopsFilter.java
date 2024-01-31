package com.paypal.infrastructure.mirakl.client.filter;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShopState;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TerminatedShopsFilter implements ShopsFilter {

	public void filterShops(final MiraklShops shops) {
		final List<MiraklShop> validShops = shops.getShops().stream().filter(this::isNotShopStatusTerminated)
				.collect(Collectors.toList());

		shops.setShops(validShops);
		shops.setTotalCount((long) validShops.size());
	}

	private boolean isNotShopStatusTerminated(final MiraklShop miraklShop) {
		return miraklShop.getState() != MiraklShopState.TERMINATED;
	}

}
