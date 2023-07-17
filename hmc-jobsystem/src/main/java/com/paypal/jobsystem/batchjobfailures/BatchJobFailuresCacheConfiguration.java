package com.paypal.jobsystem.batchjobfailures;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class BatchJobFailuresCacheConfiguration {

	@Bean
	public Caffeine<Object, Object> caffeineConfig() {
		return Caffeine.newBuilder().maximumSize(5000).expireAfterWrite(15, TimeUnit.DAYS);
	}

	@Bean
	public CacheManager cacheManager(final Caffeine<Object, Object> caffeine) {
		final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeine);
		return caffeineCacheManager;
	}

}
