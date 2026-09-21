package com.anshika.cloudops.deployment.domain;

import java.util.Objects;

public final class Deployment {

	private final DeploymentId id;
	private final DeploymentSpec specification;
	private DeploymentStatus status;

	private Deployment(DeploymentId id, DeploymentSpec specification) {
		this.id = Objects.requireNonNull(id, "id must not be null");
		this.specification = Objects.requireNonNull(specification, "specification must not be null");
		this.status = DeploymentStatus.REQUESTED;
	}

	public static Deployment request(DeploymentId id, DeploymentSpec specification) {
		return new Deployment(id, specification);
	}

	public void startDeployment() {
		transitionFrom(DeploymentStatus.REQUESTED, DeploymentStatus.DEPLOYING);
	}

	public void markRunning() {
		transitionFrom(DeploymentStatus.DEPLOYING, DeploymentStatus.RUNNING);
	}

	private void transitionFrom(DeploymentStatus expectedStatus, DeploymentStatus nextStatus) {
		if (status != expectedStatus) {
			throw new InvalidDeploymentTransitionException(status, nextStatus);
		}

		status = nextStatus;
	}

	public DeploymentId id() {
		return id;
	}

	public DeploymentSpec specification() {
		return specification;
	}

	public DeploymentStatus status() {
		return status;
	}
}
