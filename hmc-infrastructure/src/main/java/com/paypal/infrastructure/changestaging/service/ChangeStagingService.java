package com.paypal.infrastructure.changestaging.service;

import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.model.StagedChange;

import java.util.List;

public interface ChangeStagingService {

	StagedChange stageChange(Change change);

	List<StagedChange> stageChanges(List<Change> changes);

}
