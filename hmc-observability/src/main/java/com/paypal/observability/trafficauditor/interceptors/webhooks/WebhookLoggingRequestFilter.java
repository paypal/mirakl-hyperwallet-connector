package com.paypal.observability.trafficauditor.interceptors.webhooks;

import com.paypal.observability.trafficauditor.adapters.webhooks.WebhookTrafficAuditorAdapter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class WebhookLoggingRequestFilter extends OncePerRequestFilter {

	public static final int MAX_PAYLOAD_LENGTH = 10000;

	private final WebhookTrafficAuditorAdapter webhookTrafficAuditorAdapter;

	public WebhookLoggingRequestFilter(final WebhookTrafficAuditorAdapter webhookTrafficAuditorAdapter) {
		this.webhookTrafficAuditorAdapter = webhookTrafficAuditorAdapter;
	}

	@Override
	protected void doFilterInternal(@NotNull final HttpServletRequest request,
			@NotNull final HttpServletResponse response, @NotNull final FilterChain filterChain)
			throws ServletException, IOException {

		final boolean isFirstRequest = !isAsyncDispatch(request);
		HttpServletRequest requestToUse = request;

		final boolean shouldLog = shouldLog(requestToUse);

		if (shouldLog && (isFirstRequest && !(request instanceof ContentCachingRequestWrapper))) {
			requestToUse = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
		}

		try {
			filterChain.doFilter(requestToUse, response);
		}
		finally {
			if (shouldLog && isFirstRequest) {
				webhookTrafficAuditorAdapter.startTraceCapture(requestToUse);
				webhookTrafficAuditorAdapter.endTraceCapture();
			}
		}
	}

	private boolean shouldLog(final HttpServletRequest request) {
		return request.getRequestURI().contains("/webhooks/notifications") && request.getMethod().equals("POST");
	}

}
