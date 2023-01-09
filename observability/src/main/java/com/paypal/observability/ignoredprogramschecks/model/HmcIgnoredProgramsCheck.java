package com.paypal.observability.ignoredprogramschecks.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class HmcIgnoredProgramsCheck {

	private List<String> programs;

	private List<String> ignoredPrograms;

	private boolean isSubset;

}
