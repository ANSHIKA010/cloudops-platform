package com.anshika.cloudops.deployment.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.anshika.cloudops.deployment.application.port.in.GetDeploymentUseCase;
import com.anshika.cloudops.deployment.application.port.in.SubmitDeploymentCommand;
import com.anshika.cloudops.deployment.application.port.in.SubmitDeploymentUseCase;
import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/deployments")
class DeploymentController {

	private final SubmitDeploymentUseCase submitDeployment;
	private final GetDeploymentUseCase getDeployment;

	DeploymentController(
			SubmitDeploymentUseCase submitDeployment,
			GetDeploymentUseCase getDeployment) {
		this.submitDeployment = submitDeployment;
		this.getDeployment = getDeployment;
	}

	@PostMapping
	ResponseEntity<DeploymentResponse> submit(@Valid @RequestBody CreateDeploymentRequest request) {
		Deployment deployment = submitDeployment.submit(new SubmitDeploymentCommand(
				request.serviceName(),
				request.image(),
				request.replicas()));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{deploymentId}")
				.buildAndExpand(deployment.id().value())
				.toUri();

		return ResponseEntity.created(location).body(DeploymentResponse.from(deployment));
	}

	@GetMapping("/{deploymentId}")
	DeploymentResponse get(@PathVariable UUID deploymentId) {
		return DeploymentResponse.from(getDeployment.get(new DeploymentId(deploymentId)));
	}
}
