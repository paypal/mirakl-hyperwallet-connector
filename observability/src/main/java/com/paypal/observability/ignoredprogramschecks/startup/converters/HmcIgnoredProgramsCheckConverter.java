package com.paypal.observability.ignoredprogramschecks.startup.converters;

import com.paypal.observability.ignoredprogramschecks.model.HmcIgnoredProgramsCheck;
import com.paypal.observability.startupchecks.model.StartupCheck;

public interface HmcIgnoredProgramsCheckConverter {

	StartupCheck from(HmcIgnoredProgramsCheck ignoredProgramsCheck);

}
