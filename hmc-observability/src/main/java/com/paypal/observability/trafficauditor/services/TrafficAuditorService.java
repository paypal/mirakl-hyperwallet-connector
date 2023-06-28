package com.paypal.observability.trafficauditor.services;

import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;

public interface TrafficAuditorService {

	void send(TrafficAuditorTrace trafficAuditorTrace);

}
