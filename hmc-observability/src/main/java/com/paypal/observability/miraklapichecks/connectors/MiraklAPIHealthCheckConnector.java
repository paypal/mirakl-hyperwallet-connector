package com.paypal.observability.miraklapichecks.connectors;

import com.mirakl.client.mmp.domain.version.MiraklVersion;

public interface MiraklAPIHealthCheckConnector {

	MiraklVersion getVersion();

}
