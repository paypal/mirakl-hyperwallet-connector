package com.paypal.infrastructure.changestaging.service.operations;

import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StagedChangesExecutorInfo {

	private Class<?> type;

	private ChangeOperation operation;

	private ChangeTarget target;

}
