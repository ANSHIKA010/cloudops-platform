package com.anshika.cloudops.deployment.application;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anshika.cloudops.deployment.application.port.in.GetDeploymentUseCase;
import com.anshika.cloudops.deployment.application.port.in.SubmitDeploymentCommand;
import com.anshika.cloudops.deployment.application.port.in.SubmitDeploymentUseCase;
import com.anshika.cloudops.deployment.application.port.out.DeploymentIdGenerator;
import com.anshika.cloudops.deployment.application.port.out.DeploymentRepository;
import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;
import com.anshika.cloudops.deployment.domain.DeploymentSpec;

@Service
public class DeploymentApplicationService implements SubmitDeploymentUseCase, GetDeploymentUseCase {

	private final DeploymentRepository repository;
	private final DeploymentIdGenerator idGenerator;
	private final Clock clock;

	public DeploymentApplicationService(
			DeploymentRepository repository,
			DeploymentIdGenerator idGenerator,
			Clock clock) {
		this.repository = repository;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	@Transactional
	public Deployment submit(SubmitDeploymentCommand command) {
		Objects.requireNonNull(command, "command must not be null");
		DeploymentSpec specification = new DeploymentSpec(
				command.serviceName(),
				command.image(),
				command.replicas());
		Deployment deployment = Deployment.request(
				idGenerator.generate(),
				specification,
				clock.instant().truncatedTo(ChronoUnit.MICROS));

		return repository.save(deployment);
	}

	@Override
	@Transactional(readOnly = true)
	public Deployment get(DeploymentId deploymentId) {
		Objects.requireNonNull(deploymentId, "deploymentId must not be null");
		return repository.findById(deploymentId)
				.orElseThrow(() -> new DeploymentNotFoundException(deploymentId));
	}
}
