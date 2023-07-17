package com.paypal.infrastructure.changestaging.controllers;

import com.paypal.infrastructure.changestaging.controllers.converters.StagedChangesDtoConverter;
import com.paypal.infrastructure.changestaging.controllers.dtos.StagedChangeDto;
import com.paypal.infrastructure.changestaging.repositories.StagedChangesRepository;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import com.paypal.infrastructure.changestaging.service.StagedChangesPoller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/management/staged-changes")
public class ChangeStagingManagementController {

	private final StagedChangesRepository stagedChangesRepository;

	private final StagedChangesDtoConverter stagedChangesDtoConverter;

	private final StagedChangesPoller stagedChangesPoller;

	private final PagedResourcesAssembler<StagedChangeDto> pagedResourcesAssembler;

	public ChangeStagingManagementController(final StagedChangesRepository stagedChangesRepository,
			final StagedChangesDtoConverter stagedChangesDtoConverter, final StagedChangesPoller stagedChangesPoller,
			final PagedResourcesAssembler<StagedChangeDto> pagedResourcesAssembler) {
		this.stagedChangesRepository = stagedChangesRepository;
		this.stagedChangesDtoConverter = stagedChangesDtoConverter;
		this.stagedChangesPoller = stagedChangesPoller;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
	}

	@GetMapping("/")
	public PagedModel<EntityModel<StagedChangeDto>> findAll(final Pageable pageable) {
		final Page<StagedChangeEntity> stagedChanges = stagedChangesRepository.findAll(pageable);

		return pagedResourcesAssembler.toModel(stagedChangesDtoConverter.from(stagedChanges));
	}

	@PostMapping("/process")
	@ResponseStatus(OK)
	public void process() {
		stagedChangesPoller.performStagedChange();
	}

}
