package com.paypal.infrastructure.controllers;

import com.paypal.infrastructure.dto.IgnoredProgramsDTO;
import com.paypal.infrastructure.service.IgnoreProgramsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/configuration/programs")
public class ChangeIgnoredProgramsController {

	@Resource
	private IgnoreProgramsService ignoreProgramsService;

	@PutMapping("/ignored")
	public ResponseEntity<String> ignore(@RequestBody final IgnoredProgramsDTO programs) {
		ignoreProgramsService.ignorePrograms(programs.getIgnoredPrograms());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/ignored")
	public ResponseEntity<Object> get() {
		return new ResponseEntity<>(ignoreProgramsService.getIgnoredPrograms(), HttpStatus.OK);
	}

}
