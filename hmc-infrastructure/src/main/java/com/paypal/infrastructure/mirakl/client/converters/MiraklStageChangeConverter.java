package com.paypal.infrastructure.mirakl.client.converters;

import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MiraklStageChangeConverter {

	default Change from(final MiraklUpdateShop source) {
		final Change change = new Change();
		change.setType(MiraklUpdateShop.class);
		change.setPayload(source);
		change.setOperation(ChangeOperation.UPDATE);
		change.setTarget(ChangeTarget.MIRAKL);

		return change;
	}

	List<Change> from(List<MiraklUpdateShop> source);

}
