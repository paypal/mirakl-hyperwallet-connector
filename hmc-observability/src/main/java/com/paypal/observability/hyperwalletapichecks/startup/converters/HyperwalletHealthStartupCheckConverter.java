package com.paypal.observability.hyperwalletapichecks.startup.converters;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.startupchecks.model.StartupCheck;

public interface HyperwalletHealthStartupCheckConverter {

	StartupCheck from(HyperwalletAPICheck hyperwalletAPICheck);

}
