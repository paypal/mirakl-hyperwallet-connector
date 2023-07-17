package com.paypal.observability.trafficauditor.adapters;

public interface TrafficAuditorAdapter<T, R> {

	void startTraceCapture(T request);

	void endTraceCapture(R response);

	void endTraceCapture();

	void endTraceCapture(Throwable thrown);

}
