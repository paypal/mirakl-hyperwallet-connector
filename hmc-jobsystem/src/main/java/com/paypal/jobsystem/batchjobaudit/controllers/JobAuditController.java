package com.paypal.jobsystem.batchjobaudit.controllers;

import com.paypal.jobsystem.batchjobaudit.controllers.converters.BatchJobItemTrackInfoEntityConverter;
import com.paypal.jobsystem.batchjobaudit.controllers.converters.BatchJobTrackInfoEntityConverter;
import com.paypal.jobsystem.batchjobaudit.controllers.dto.BatchJobItemTrackInfoResponse;
import com.paypal.jobsystem.batchjobaudit.controllers.dto.BatchJobTrackInfoResponse;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/batchjob-audit")
public class JobAuditController {

	@Autowired
	private BatchJobItemTrackInfoEntityConverter batchJobItemTrackInfoEntityConverter;

	@Autowired
	private BatchJobTrackInfoEntityConverter batchJobTrackInfoEntityConverter;

	@Autowired
	private BatchJobTrackingService batchJobTrackingService;

	@GetMapping("/")
	public List<BatchJobTrackInfoResponse> getAllJobs(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime to) {
		final var batchJobTrackInfoEntities = batchJobTrackingService.getJobTrackingEntries(from, to);
		return batchJobTrackInfoEntityConverter.toResponse(batchJobTrackInfoEntities);
	}

	@GetMapping("/{id}/items")
	public List<BatchJobItemTrackInfoResponse> getJobItems(@PathVariable final String id) {
		final var batchJobItemTrackInfoEntities = batchJobTrackingService.getJobItemTrackingEntries(id);
		return batchJobItemTrackInfoEntityConverter.toResponse(batchJobItemTrackInfoEntities);
	}

}
