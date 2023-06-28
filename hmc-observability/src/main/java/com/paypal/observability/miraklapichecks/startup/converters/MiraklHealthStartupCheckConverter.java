package com.paypal.observability.miraklapichecks.startup.converters;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.startupchecks.model.StartupCheck;

public interface MiraklHealthStartupCheckConverter {

	StartupCheck from(MiraklAPICheck miraklAPICheck);

}
