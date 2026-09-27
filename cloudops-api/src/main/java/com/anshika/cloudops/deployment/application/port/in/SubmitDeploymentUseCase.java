package com.anshika.cloudops.deployment.application.port.in;

import com.anshika.cloudops.deployment.domain.Deployment;

public interface SubmitDeploymentUseCase {

	Deployment submit(SubmitDeploymentCommand command);
}
