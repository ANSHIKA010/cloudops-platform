package com.anshika.cloudops.deployment.application;

import com.anshika.cloudops.deployment.domain.DeploymentId;

public final class DeploymentNotFoundException extends RuntimeException {

	private final DeploymentId deploymentId;

	public DeploymentNotFoundException(DeploymentId deploymentId) {
		super("Deployment %s was not found".formatted(deploymentId.value()));
		this.deploymentId = deploymentId;
	}

	public DeploymentId deploymentId() {
		return deploymentId;
	}
}
