package com.anshika.cloudops.deployment.adapter.in.web;

import com.anshika.cloudops.deployment.domain.DeploymentSpec;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record CreateDeploymentRequest(
		@NotBlank(message = "serviceName must not be blank")
		@Size(max = DeploymentSpec.MAX_SERVICE_NAME_LENGTH,
				message = "serviceName must not exceed {max} characters")
		String serviceName,

		@NotBlank(message = "image must not be blank")
		@Size(max = DeploymentSpec.MAX_IMAGE_LENGTH,
				message = "image must not exceed {max} characters")
		String image,

		@Min(value = DeploymentSpec.MIN_REPLICAS,
				message = "replicas must be at least {value}")
		@Max(value = DeploymentSpec.MAX_REPLICAS,
				message = "replicas must not exceed {value}")
		int replicas) {
}
