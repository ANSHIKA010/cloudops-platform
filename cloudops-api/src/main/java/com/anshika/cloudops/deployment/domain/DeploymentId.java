package com.anshika.cloudops.deployment.domain;

import java.util.Objects;
import java.util.UUID;

public record DeploymentId(UUID value) {

	public DeploymentId {
		Objects.requireNonNull(value, "value must not be null");
	}

	public static DeploymentId generate() {
		return new DeploymentId(UUID.randomUUID());
	}
}
