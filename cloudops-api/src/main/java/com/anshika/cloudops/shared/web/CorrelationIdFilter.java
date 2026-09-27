package com.anshika.cloudops.shared.web;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

	public static final String HEADER_NAME = "X-Correlation-ID";
	public static final String REQUEST_ATTRIBUTE = CorrelationIdFilter.class.getName() + ".correlationId";
	private static final Pattern SAFE_CORRELATION_ID = Pattern.compile("[A-Za-z0-9._-]{1,128}");

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String correlationId = selectCorrelationId(request.getHeader(HEADER_NAME));
		request.setAttribute(REQUEST_ATTRIBUTE, correlationId);
		response.setHeader(HEADER_NAME, correlationId);

		try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", correlationId)) {
			filterChain.doFilter(request, response);
		}
	}

	private static String selectCorrelationId(String candidate) {
		if (candidate != null && SAFE_CORRELATION_ID.matcher(candidate).matches()) {
			return candidate;
		}
		return UUID.randomUUID().toString();
	}
}
