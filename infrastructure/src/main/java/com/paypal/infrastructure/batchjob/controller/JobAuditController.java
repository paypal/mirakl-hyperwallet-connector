package com.paypal.infrastructure.batchjob.controller;

import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.infrastructure.batchjob.converters.BatchJobItemTrackInfoEntityConverter;
import com.paypal.infrastructure.batchjob.converters.BatchJobTrackInfoEntityConverter;
import com.paypal.infrastructure.batchjob.dto.BatchJobItemTrackInfoResponse;
import com.paypal.infrastructure.batchjob.dto.BatchJobTrackInfoResponse;
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
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
		var batchJobTrackInfoEntities = batchJobTrackingService.getJobTrackingEntries(from, to);
		return batchJobTrackInfoEntityConverter.toResponse(batchJobTrackInfoEntities);
	}

	@GetMapping("/{id}/items")
	public List<BatchJobItemTrackInfoResponse> getJobItems(@PathVariable String id) {
		var batchJobItemTrackInfoEntities = batchJobTrackingService.getJobItemTrackingEntries(id);
		return batchJobItemTrackInfoEntityConverter.toResponse(batchJobItemTrackInfoEntities);
	}

}
