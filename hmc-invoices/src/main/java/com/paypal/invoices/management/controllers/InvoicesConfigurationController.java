package com.paypal.invoices.management.controllers;

import com.paypal.invoices.management.controllers.dto.CommissionsConfigurationDto;
import com.paypal.invoices.management.services.InvoicesConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuration/invoices")
public class InvoicesConfigurationController {

	private final InvoicesConfigurationService invoicesConfigurationService;

	public InvoicesConfigurationController(final InvoicesConfigurationService invoicesConfigurationService) {
		this.invoicesConfigurationService = invoicesConfigurationService;
	}

	@PutMapping("/commissions")
	public ResponseEntity<String> setCommissions(
			@RequestBody final CommissionsConfigurationDto commissionsConfiguration) {
		invoicesConfigurationService
				.setOperatorCommissionsEnabled(commissionsConfiguration.isOperatorCommissionsEnabled());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/commissions")
	public ResponseEntity<CommissionsConfigurationDto> getCommissions() {
		return new ResponseEntity<>(
				new CommissionsConfigurationDto(invoicesConfigurationService.isOperatorCommissionsEnabled()),
				HttpStatus.OK);
	}

}
