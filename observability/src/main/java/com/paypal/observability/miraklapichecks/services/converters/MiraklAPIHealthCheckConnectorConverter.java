package com.paypal.observability.miraklapichecks.services.converters;

import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;

public interface MiraklAPIHealthCheckConnectorConverter {

	MiraklAPICheck from(MiraklVersion miraklVersion);

	MiraklAPICheck from(Exception e);

}
