package com.anshika.cloudops.deployment.application.port.out;

import java.util.Optional;

import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;

public interface DeploymentRepository {

	Deployment save(Deployment deployment);

	Optional<Deployment> findById(DeploymentId deploymentId);
}
