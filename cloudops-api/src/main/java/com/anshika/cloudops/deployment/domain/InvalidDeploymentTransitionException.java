package com.anshika.cloudops.deployment.domain;

public final class InvalidDeploymentTransitionException extends RuntimeException {

	private final DeploymentStatus currentStatus;
	private final DeploymentStatus requestedStatus;

	public InvalidDeploymentTransitionException(
			DeploymentStatus currentStatus,
			DeploymentStatus requestedStatus) {
		super("Deployment cannot transition from %s to %s".formatted(currentStatus, requestedStatus));
		this.currentStatus = currentStatus;
		this.requestedStatus = requestedStatus;
	}

	public DeploymentStatus currentStatus() {
		return currentStatus;
	}

	public DeploymentStatus requestedStatus() {
		return requestedStatus;
	}
}
