package com.anshika.cloudops.deployment.application.port.out;

import com.anshika.cloudops.deployment.domain.DeploymentId;

public interface DeploymentIdGenerator {

	DeploymentId generate();
}
