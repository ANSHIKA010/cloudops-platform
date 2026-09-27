package com.anshika.cloudops.deployment.application.port.in;

import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;

public interface GetDeploymentUseCase {

	Deployment get(DeploymentId deploymentId);
}
