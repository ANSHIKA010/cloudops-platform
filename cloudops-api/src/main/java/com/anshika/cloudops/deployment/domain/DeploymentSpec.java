package com.anshika.cloudops.deployment.domain;

public record DeploymentSpec(String serviceName, String image, int replicas) {

	public static final int MAX_SERVICE_NAME_LENGTH = 100;
	public static final int MAX_IMAGE_LENGTH = 255;
	public static final int MIN_REPLICAS = 1;
	public static final int MAX_REPLICAS = 5;

	public DeploymentSpec {
		serviceName = requireText(serviceName, "serviceName", MAX_SERVICE_NAME_LENGTH);
		image = requireText(image, "image", MAX_IMAGE_LENGTH);

		if (replicas < MIN_REPLICAS || replicas > MAX_REPLICAS) {
			throw new IllegalArgumentException(
					"replicas must be between %d and %d".formatted(MIN_REPLICAS, MAX_REPLICAS));
		}
	}

	private static String requireText(String value, String field, int maximumLength) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " must not be blank");
		}

		String normalizedValue = value.trim();
		if (normalizedValue.length() > maximumLength) {
			throw new IllegalArgumentException(field + " must not exceed " + maximumLength + " characters");
		}

		return normalizedValue;
	}
}
