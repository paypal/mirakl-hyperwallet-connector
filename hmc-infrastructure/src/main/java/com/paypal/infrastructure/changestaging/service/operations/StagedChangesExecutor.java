package com.paypal.infrastructure.changestaging.service.operations;

import com.paypal.infrastructure.changestaging.model.StagedChange;

import java.util.List;

public interface StagedChangesExecutor {

	void execute(List<StagedChange> changes);

	StagedChangesExecutorInfo getExecutorInfo();

}
