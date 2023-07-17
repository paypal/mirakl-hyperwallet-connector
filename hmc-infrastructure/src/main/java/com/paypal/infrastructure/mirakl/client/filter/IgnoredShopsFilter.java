package com.paypal.infrastructure.mirakl.client.filter;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.infrastructure.mirakl.support.MiraklShopUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IgnoredShopsFilter {

	private final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration;

	public IgnoredShopsFilter(final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration) {
		this.hyperwalletProgramsConfiguration = hyperwalletProgramsConfiguration;
	}

	public MiraklShops filterIgnoredShops(final MiraklShops shops) {
		final List<MiraklShop> validShops = shops.getShops().stream().filter(Predicate.not(this::isIgnored))
				.collect(Collectors.toList());
		shops.setShops(validShops);
		shops.setTotalCount((long) validShops.size());

		return shops;
	}

	private boolean isIgnored(final MiraklShop miraklShop) {
		final Optional<String> program = MiraklShopUtils.getProgram(miraklShop);

		if (program.isPresent()) {
			final String programValue = program.get();
			final boolean isIgnored = hyperwalletProgramsConfiguration.getProgramConfiguration(programValue)
					.isIgnored();
			if (isIgnored) {
				log.info("Shop with id [{}] contains program [{}] which is in the ignored list, skipping processing",
						miraklShop.getId(), programValue);
			}
			return isIgnored;
		}
		else {
			log.debug("Program not set for shop with id [{}]", miraklShop.getId());
			return true;
		}
	}

}
