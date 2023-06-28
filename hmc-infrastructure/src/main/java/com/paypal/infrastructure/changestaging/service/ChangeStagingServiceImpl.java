package com.paypal.infrastructure.changestaging.service;

import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import com.paypal.infrastructure.changestaging.repositories.StagedChangesRepository;
import com.paypal.infrastructure.changestaging.service.converters.StagedChangesEntityConverter;
import com.paypal.infrastructure.changestaging.service.converters.StagedChangesModelConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChangeStagingServiceImpl implements ChangeStagingService {

	private final StagedChangesModelConverter stagedChangesModelConverter;

	private final StagedChangesEntityConverter stagedChangesEntityConverter;

	private final StagedChangesRepository stagedChangeRepository;

	public ChangeStagingServiceImpl(final StagedChangesModelConverter stagedChangesModelConverter,
			final StagedChangesEntityConverter stagedChangesEntityConverter,
			final StagedChangesRepository stagedChangeRepository) {
		this.stagedChangesModelConverter = stagedChangesModelConverter;
		this.stagedChangesEntityConverter = stagedChangesEntityConverter;
		this.stagedChangeRepository = stagedChangeRepository;
	}

	@Override
	public StagedChange stageChange(final Change change) {
		final StagedChange stagedChange = stagedChangesModelConverter.from(change);
		stagedChangeRepository.save(stagedChangesEntityConverter.from(stagedChange));
		return stagedChange;
	}

	@Override
	public List<StagedChange> stageChanges(final List<Change> changes) {
		return changes.stream().map(this::stageChange).toList();
	}

}
