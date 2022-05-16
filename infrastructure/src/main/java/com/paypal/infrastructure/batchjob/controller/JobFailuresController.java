package com.paypal.infrastructure.batchjob.controller;

import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.converters.BatchJobFailedItemResponseConverter;
import com.paypal.infrastructure.batchjob.dto.BatchJobFailedItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/batchjob-failed-items")
public class JobFailuresController {

	@Autowired
	private BatchJobFailedItemService batchJobFailedItemService;

	@Autowired
	private BatchJobFailedItemResponseConverter batchJobFailedItemResponseConverter;

	@GetMapping("/{itemType}")
	public List<BatchJobFailedItemResponse> getFailedItems(@PathVariable String itemType) {
		var batchJobFailedItems = batchJobFailedItemService.getFailedItems(itemType);

		return batchJobFailedItemResponseConverter.toResponse(batchJobFailedItems);
	}

}
