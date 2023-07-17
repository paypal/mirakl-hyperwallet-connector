package com.paypal.infrastructure.mirakl.client.changestaging;

import com.mirakl.client.core.internal.util.Patch;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesExecutor;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesExecutorInfo;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static org.reflections.ReflectionUtils.withName;

//@formatter:off
@Slf4j
@Component
public class MiraklUpdateShopStagedChangesExecutor implements StagedChangesExecutor {

	private final MiraklClient miraklClient;

	public MiraklUpdateShopStagedChangesExecutor(@Qualifier("directMiraklClient") final MiraklClient miraklClient) {
		this.miraklClient = miraklClient;
	}

	@Override
	public void execute(final List<StagedChange> changes) {
		changes.sort(Comparator.comparing(this::getShopId)
				.thenComparing(StagedChange::getCreationDate));
		final List<MiraklUpdateShop> updateShops = changes.stream()
				.map(StagedChange::getPayload)
				.map(MiraklUpdateShop.class::cast)
				.toList();
		final List<MiraklUpdateShop> compactedShops = compactShops(regroupShops(updateShops));
		log.info("Updating {} shops with ids {}",
				compactedShops.size(), compactedShops.stream()
				.map(MiraklUpdateShop::getShopId)
				.toList());
		miraklClient.updateShops(new MiraklUpdateShopsRequest(compactedShops));
	}

	private Long getShopId(final StagedChange stagedChange) {
		return ((MiraklUpdateShop) stagedChange.getPayload()).getShopId();
	}

	@Override
	public StagedChangesExecutorInfo getExecutorInfo() {
		final StagedChangesExecutorInfo stagedChangesExecutorInfo = new StagedChangesExecutorInfo();
		stagedChangesExecutorInfo.setOperation(ChangeOperation.UPDATE);
		stagedChangesExecutorInfo.setTarget(ChangeTarget.MIRAKL);
		stagedChangesExecutorInfo.setType(MiraklUpdateShop.class);

		return stagedChangesExecutorInfo;
	}

	private List<MiraklUpdateShop> compactShops(final List<List<MiraklUpdateShop>> shops) {
		return shops.stream().map(this::compactShopChanges).toList();
	}

	private MiraklUpdateShop compactShopChanges(final List<MiraklUpdateShop> shopChanges) {
		return shopChanges.stream().reduce(new MiraklUpdateShop(), this::compactShop);
	}

	private MiraklUpdateShop compactShop(final MiraklUpdateShop miraklUpdateShop1, final MiraklUpdateShop miraklUpdateShop2) {
		copyAllValues(miraklUpdateShop2, miraklUpdateShop1);
		copyAllPatches(miraklUpdateShop2, miraklUpdateShop1);

		return miraklUpdateShop1;
	}

	private List<List<MiraklUpdateShop>> regroupShops(final List<MiraklUpdateShop> shops) {
		return shops.stream().collect(Collectors
				.groupingBy(MiraklUpdateShop::getShopId))
				.values()
				.stream().toList();
	}

	private void copyAllValues(final MiraklUpdateShop source, final MiraklUpdateShop target) {
		Arrays.stream(PropertyUtils.getPropertyDescriptors(MiraklUpdateShop.class))
				.filter(d -> !d.getName().contains("Patch"))
				.filter(not(this::hasPatchType))
				.forEach(d -> copyAsValue(source, target, d.getName()));
	}

	private void copyAllPatches(final MiraklUpdateShop source, final MiraklUpdateShop target) {
		Arrays.stream(PropertyUtils.getPropertyDescriptors(MiraklUpdateShop.class))
				.filter(this::hasPatchType)
				.forEach(d -> copyAsPatch(source, target, d.getName()));
	}

	@SuppressWarnings("unchecked")
	private boolean hasPatchType(final PropertyDescriptor property) {
		return !ReflectionUtils.getAllFields(MiraklUpdateShop.class,
						f -> f.getName().contains(property.getName()),
						f -> f.getType().isAssignableFrom(Patch.class))
				.isEmpty();
	}

	@SneakyThrows
	private void copyAsValue(final MiraklUpdateShop source, final MiraklUpdateShop target, final String fieldName) {
		final Object sourceValue = PropertyUtils.getProperty(source, fieldName);
		if (sourceValue != null) {
			PropertyUtils.setProperty(target, fieldName, sourceValue);
		}
	}

	@SuppressWarnings("java:S3011")
	@SneakyThrows
	private void copyAsPatch(final MiraklUpdateShop source, final MiraklUpdateShop target, final String fieldName) {
		final List<Field> patchObjectFields = new ArrayList<>(ReflectionUtils.getAllFields(MiraklUpdateShop.class, withName(fieldName)));
		patchObjectFields.sort(Comparator.comparing(Field::getDeclaringClass,
				MiraklUpdateShopStagedChangesExecutor::compareClassesByHierarchy));
		final Field patchObjectField = patchObjectFields.get(0);
		patchObjectField.setAccessible(true);
		final Patch<?> patchObject = (Patch<?>) patchObjectField.get(source);
		if (patchObject != null && patchObject.isPresent()) {
			copyAsValue(source, target, fieldName);
		}
		patchObjectField.setAccessible(false);
	}

	private static int compareClassesByHierarchy(final Class<?> c1, final Class<?> c2) {
		if (c1.isAssignableFrom(c2)) {
			return 1;
		}
		else if (c2.isAssignableFrom(c1)) {
			return -1;
		}
		return 0;
	}

}
