package com.anshika.cloudops.deployment.domain;

import java.time.Instant;
import java.util.Objects;

public final class Deployment {

	private final DeploymentId id;
	private final DeploymentSpec specification;
	private DeploymentStatus status;
	private final Instant createdAt;
	private Instant updatedAt;

	private Deployment(
			DeploymentId id,
			DeploymentSpec specification,
			DeploymentStatus status,
			Instant createdAt,
			Instant updatedAt) {
		this.id = Objects.requireNonNull(id, "id must not be null");
		this.specification = Objects.requireNonNull(specification, "specification must not be null");
		this.status = Objects.requireNonNull(status, "status must not be null");
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
		this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
		if (updatedAt.isBefore(createdAt)) {
			throw new IllegalArgumentException("updatedAt must not be before createdAt");
		}
	}

	public static Deployment request(
			DeploymentId id,
			DeploymentSpec specification,
			Instant requestedAt) {
		return new Deployment(
				id,
				specification,
				DeploymentStatus.REQUESTED,
				requestedAt,
				requestedAt);
	}

	public static Deployment rehydrate(
			DeploymentId id,
			DeploymentSpec specification,
			DeploymentStatus status,
			Instant createdAt,
			Instant updatedAt) {
		return new Deployment(id, specification, status, createdAt, updatedAt);
	}

	public void startDeployment(Instant transitionedAt) {
		transitionFrom(DeploymentStatus.REQUESTED, DeploymentStatus.DEPLOYING, transitionedAt);
	}

	public void markRunning(Instant transitionedAt) {
		transitionFrom(DeploymentStatus.DEPLOYING, DeploymentStatus.RUNNING, transitionedAt);
	}

	private void transitionFrom(
			DeploymentStatus expectedStatus,
			DeploymentStatus nextStatus,
			Instant transitionedAt) {
		if (status != expectedStatus) {
			throw new InvalidDeploymentTransitionException(status, nextStatus);
		}
		Objects.requireNonNull(transitionedAt, "transitionedAt must not be null");
		if (transitionedAt.isBefore(updatedAt)) {
			throw new IllegalArgumentException("transitionedAt must not be before updatedAt");
		}

		status = nextStatus;
		updatedAt = transitionedAt;
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

	public Instant createdAt() {
		return createdAt;
	}

	public Instant updatedAt() {
		return updatedAt;
	}
}
