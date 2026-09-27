package com.anshika.cloudops.deployment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class DeploymentTest {

	private static final DeploymentId DEPLOYMENT_ID =
			new DeploymentId(UUID.fromString("5f635fe8-5765-4bc1-9e76-f11070410b11"));
	private static final DeploymentSpec SPECIFICATION =
			new DeploymentSpec("orders-api", "registry.example.com/orders:1.0.0", 2);
	private static final Instant REQUESTED_AT = Instant.parse("2026-09-27T08:00:00Z");

	@Test
	void newDeploymentStartsRequested() {
		Deployment deployment = Deployment.request(DEPLOYMENT_ID, SPECIFICATION, REQUESTED_AT);

		assertThat(deployment.id()).isEqualTo(DEPLOYMENT_ID);
		assertThat(deployment.specification()).isEqualTo(SPECIFICATION);
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.REQUESTED);
		assertThat(deployment.createdAt()).isEqualTo(REQUESTED_AT);
		assertThat(deployment.updatedAt()).isEqualTo(REQUESTED_AT);
	}

	@Test
	void deploymentCanMoveFromRequestedToDeployingToRunning() {
		Deployment deployment = requestedDeployment();

		Instant deployingAt = REQUESTED_AT.plusSeconds(5);
		deployment.startDeployment(deployingAt);
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.DEPLOYING);
		assertThat(deployment.updatedAt()).isEqualTo(deployingAt);

		Instant runningAt = deployingAt.plusSeconds(5);
		deployment.markRunning(runningAt);
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.RUNNING);
		assertThat(deployment.updatedAt()).isEqualTo(runningAt);
	}

	@Test
	void deploymentCannotBecomeRunningBeforeItStarts() {
		Deployment deployment = requestedDeployment();

		assertThatThrownBy(() -> deployment.markRunning(REQUESTED_AT.plusSeconds(1)))
				.isInstanceOf(InvalidDeploymentTransitionException.class)
				.hasMessage("Deployment cannot transition from REQUESTED to RUNNING")
				.extracting("currentStatus", "requestedStatus")
				.containsExactly(DeploymentStatus.REQUESTED, DeploymentStatus.RUNNING);

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.REQUESTED);
	}

	@Test
	void deploymentCannotStartTwice() {
		Deployment deployment = requestedDeployment();
		deployment.startDeployment(REQUESTED_AT.plusSeconds(1));

		assertThatThrownBy(() -> deployment.startDeployment(REQUESTED_AT.plusSeconds(2)))
				.isInstanceOf(InvalidDeploymentTransitionException.class)
				.hasMessage("Deployment cannot transition from DEPLOYING to DEPLOYING");

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.DEPLOYING);
	}

	@Test
	void runningDeploymentCannotReturnToDeploying() {
		Deployment deployment = requestedDeployment();
		deployment.startDeployment(REQUESTED_AT.plusSeconds(1));
		deployment.markRunning(REQUESTED_AT.plusSeconds(2));

		assertThatThrownBy(() -> deployment.startDeployment(REQUESTED_AT.plusSeconds(3)))
				.isInstanceOf(InvalidDeploymentTransitionException.class)
				.hasMessage("Deployment cannot transition from RUNNING to DEPLOYING");

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.RUNNING);
	}

	@Test
	void transitionTimestampCannotMoveBackward() {
		Deployment deployment = requestedDeployment();

		assertThatThrownBy(() -> deployment.startDeployment(REQUESTED_AT.minusSeconds(1)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("transitionedAt must not be before updatedAt");

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.REQUESTED);
		assertThat(deployment.updatedAt()).isEqualTo(REQUESTED_AT);
	}

	private static Deployment requestedDeployment() {
		return Deployment.request(DEPLOYMENT_ID, SPECIFICATION, REQUESTED_AT);
	}
}
