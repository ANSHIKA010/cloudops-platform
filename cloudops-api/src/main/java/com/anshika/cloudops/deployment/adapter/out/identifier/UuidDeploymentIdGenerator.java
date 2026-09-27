package com.anshika.cloudops.deployment.adapter.out.identifier;

import org.springframework.stereotype.Component;

import com.anshika.cloudops.deployment.application.port.out.DeploymentIdGenerator;
import com.anshika.cloudops.deployment.domain.DeploymentId;

@Component
class UuidDeploymentIdGenerator implements DeploymentIdGenerator {

	@Override
	public DeploymentId generate() {
		return DeploymentId.generate();
	}
}
