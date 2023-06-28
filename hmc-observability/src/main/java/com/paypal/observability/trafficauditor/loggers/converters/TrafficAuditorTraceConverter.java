package com.paypal.observability.trafficauditor.loggers.converters;

import com.paypal.observability.trafficauditor.loggers.dtos.TrafficAuditorRequestLog;
import com.paypal.observability.trafficauditor.loggers.dtos.TrafficAuditorResponseLog;
import com.paypal.observability.trafficauditor.loggers.dtos.TrafficAuditorTraceLog;
import com.paypal.observability.trafficauditor.model.TrafficAuditorRequest;
import com.paypal.observability.trafficauditor.model.TrafficAuditorResponse;
import com.paypal.observability.trafficauditor.model.TrafficAuditorTrace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface TrafficAuditorTraceConverter {

	@Mapping(target = "throwableMessage", source = "throwable")
	TrafficAuditorTraceLog from(TrafficAuditorTrace source);

	TrafficAuditorRequestLog from(TrafficAuditorRequest source);

	TrafficAuditorResponseLog from(TrafficAuditorResponse source);

	default TrafficAuditorRequest fromOptionalTrafficAuditorRequest(final Optional<TrafficAuditorRequest> value) {
		return value.orElse(null);
	}

	default TrafficAuditorResponse fromOptionalTrafficAuditorResponse(final Optional<TrafficAuditorResponse> value) {
		return value.orElse(null);
	}

	default String from(final Optional<Throwable> source) {
		return source.map(t -> "%s: %s".formatted(t.getClass().getName(), t.getMessage())).orElse(null);
	}

}
