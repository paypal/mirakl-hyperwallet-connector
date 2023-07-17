package com.paypal.jobsystem.batchjobfailures.aspects;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
public class BatchJobFailedItemServiceCacheAspect {

	private final BatchJobFailedItemCacheService batchJobFailedItemCacheService;

	public BatchJobFailedItemServiceCacheAspect(final BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		this.batchJobFailedItemCacheService = batchJobFailedItemCacheService;
	}

	@Before("execution(* com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService.saveItemFailed(..))")
	public void beforeSaveItem(final JoinPoint jp) {
		final BatchJobItem<?> item = (BatchJobItem<?>) jp.getArgs()[0];
		batchJobFailedItemCacheService.storeItem(item);
	}

	@Before("execution(* com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService.removeItemProcessed(..))")
	public void beforeRemoveItem(final JoinPoint jp) {
		final BatchJobItem<?> item = (BatchJobItem<?>) jp.getArgs()[0];
		batchJobFailedItemCacheService.removeItem(item.getItemType(), item.getItemId());
	}

	@SuppressWarnings("unchecked")
	@Before("execution(* com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService.checkUpdatedFailedItems(..))")
	public void beforeCheckUpdatedItems(final JoinPoint jp) {
		final Collection<BatchJobItem<?>> extractedItems = (Collection<BatchJobItem<?>>) jp.getArgs()[0];
		batchJobFailedItemCacheService.refreshCachedItems(extractedItems);
	}

}
