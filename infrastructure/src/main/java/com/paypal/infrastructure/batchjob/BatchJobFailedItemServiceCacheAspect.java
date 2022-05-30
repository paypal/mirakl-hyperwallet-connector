package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
public class BatchJobFailedItemServiceCacheAspect {

	private final BatchJobFailedItemCacheService batchJobFailedItemCacheService;

	public BatchJobFailedItemServiceCacheAspect(BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		this.batchJobFailedItemCacheService = batchJobFailedItemCacheService;
	}

	@Before("execution(* com.paypal.infrastructure.batchjob.BatchJobFailedItemService.saveItemFailed(..))")
	public void beforeSaveItem(JoinPoint jp) {
		BatchJobItem<?> item = (BatchJobItem<?>) jp.getArgs()[0];
		batchJobFailedItemCacheService.storeItem(item);
	}

	@Before("execution(* com.paypal.infrastructure.batchjob.BatchJobFailedItemService.removeItemProcessed(..))")
	public void beforeRemoveItem(JoinPoint jp) {
		BatchJobItem<?> item = (BatchJobItem<?>) jp.getArgs()[0];
		batchJobFailedItemCacheService.removeItem(item.getItemType(), item.getItemId());
	}

	@SuppressWarnings("unchecked")
	@Before("execution(* com.paypal.infrastructure.batchjob.BatchJobFailedItemService.checkUpdatedFailedItems(..))")
	public void beforeCheckUpdatedItems(JoinPoint jp) {
		Collection<BatchJobItem<?>> extractedItems = (Collection<BatchJobItem<?>>) jp.getArgs()[0];
		batchJobFailedItemCacheService.refreshCachedItems(extractedItems);
	}

}
