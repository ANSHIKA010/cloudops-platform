package com.anshika.cloudops.deployment.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentStatus;

record DeploymentResponse(
		UUID id,
		String serviceName,
		String image,
		int replicas,
		DeploymentStatus status,
		Instant createdAt,
		Instant updatedAt) {

	static DeploymentResponse from(Deployment deployment) {
		return new DeploymentResponse(
				deployment.id().value(),
				deployment.specification().serviceName(),
				deployment.specification().image(),
				deployment.specification().replicas(),
				deployment.status(),
				deployment.createdAt(),
				deployment.updatedAt());
	}
}
