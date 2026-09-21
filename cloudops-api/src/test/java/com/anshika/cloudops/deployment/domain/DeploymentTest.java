package com.anshika.cloudops.deployment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DeploymentTest {

	private static final DeploymentId DEPLOYMENT_ID =
			new DeploymentId(UUID.fromString("5f635fe8-5765-4bc1-9e76-f11070410b11"));
	private static final DeploymentSpec SPECIFICATION =
			new DeploymentSpec("orders-api", "registry.example.com/orders:1.0.0", 2);

	@Test
	void newDeploymentStartsRequested() {
		Deployment deployment = Deployment.request(DEPLOYMENT_ID, SPECIFICATION);

		assertThat(deployment.id()).isEqualTo(DEPLOYMENT_ID);
		assertThat(deployment.specification()).isEqualTo(SPECIFICATION);
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.REQUESTED);
	}

	@Test
	void deploymentCanMoveFromRequestedToDeployingToRunning() {
		Deployment deployment = requestedDeployment();

		deployment.startDeployment();
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.DEPLOYING);

		deployment.markRunning();
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.RUNNING);
	}

	@Test
	void deploymentCannotBecomeRunningBeforeItStarts() {
		Deployment deployment = requestedDeployment();

		assertThatThrownBy(deployment::markRunning)
				.isInstanceOf(InvalidDeploymentTransitionException.class)
				.hasMessage("Deployment cannot transition from REQUESTED to RUNNING")
				.extracting("currentStatus", "requestedStatus")
				.containsExactly(DeploymentStatus.REQUESTED, DeploymentStatus.RUNNING);

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.REQUESTED);
	}

	@Test
	void deploymentCannotStartTwice() {
		Deployment deployment = requestedDeployment();
		deployment.startDeployment();

		assertThatThrownBy(deployment::startDeployment)
				.isInstanceOf(InvalidDeploymentTransitionException.class)
				.hasMessage("Deployment cannot transition from DEPLOYING to DEPLOYING");

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.DEPLOYING);
	}

	@Test
	void runningDeploymentCannotReturnToDeploying() {
		Deployment deployment = requestedDeployment();
		deployment.startDeployment();
		deployment.markRunning();

		assertThatThrownBy(deployment::startDeployment)
				.isInstanceOf(InvalidDeploymentTransitionException.class)
				.hasMessage("Deployment cannot transition from RUNNING to DEPLOYING");

		assertThat(deployment.status()).isEqualTo(DeploymentStatus.RUNNING);
	}

	private static Deployment requestedDeployment() {
		return Deployment.request(DEPLOYMENT_ID, SPECIFICATION);
	}
}
