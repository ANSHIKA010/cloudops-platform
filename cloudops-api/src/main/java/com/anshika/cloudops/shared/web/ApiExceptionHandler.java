package com.anshika.cloudops.shared.web;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.anshika.cloudops.deployment.application.DeploymentNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
class ApiExceptionHandler {

	private static final String PROBLEM_BASE = "https://cloudops.example/problems/";

	@ExceptionHandler(DeploymentNotFoundException.class)
	ResponseEntity<ProblemDetail> handleNotFound(
			DeploymentNotFoundException exception,
			HttpServletRequest request) {
		return problem(
				HttpStatus.NOT_FOUND,
				"deployment-not-found",
				"Deployment not found",
				"DEPLOYMENT_NOT_FOUND",
				exception.getMessage(),
				request,
				Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ProblemDetail> handleValidation(
			MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return problem(
				HttpStatus.BAD_REQUEST,
				"request-validation-failed",
				"Request validation failed",
				"REQUEST_VALIDATION_FAILED",
				"One or more request fields are invalid.",
				request,
				Map.of("errors", errors));
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	ResponseEntity<ProblemDetail> handleMalformedRequest(
			Exception exception,
			HttpServletRequest request) {
		return problem(
				HttpStatus.BAD_REQUEST,
				"malformed-request",
				"Malformed request",
				"MALFORMED_REQUEST",
				"The request body or path contains an invalid value.",
				request,
				Map.of());
	}

	private static ResponseEntity<ProblemDetail> problem(
			HttpStatus status,
			String type,
			String title,
			String code,
			String detail,
			HttpServletRequest request,
			Map<String, ?> properties) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		problem.setType(URI.create(PROBLEM_BASE + type));
		problem.setTitle(title);
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("code", code);
		problem.setProperty("correlationId", correlationId(request));
		properties.forEach(problem::setProperty);
		return ResponseEntity.status(status).body(problem);
	}

	private static String correlationId(HttpServletRequest request) {
		Object correlationId = request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE);
		return correlationId == null ? "unavailable" : correlationId.toString();
	}
}
