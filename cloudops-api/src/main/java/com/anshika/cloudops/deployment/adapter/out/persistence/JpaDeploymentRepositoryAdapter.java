package com.anshika.cloudops.deployment.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.anshika.cloudops.deployment.application.port.out.DeploymentRepository;
import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;

@Repository
class JpaDeploymentRepositoryAdapter implements DeploymentRepository {

	private final SpringDataDeploymentRepository repository;

	JpaDeploymentRepositoryAdapter(SpringDataDeploymentRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Deployment save(Deployment deployment) {
		DeploymentJpaEntity entity = repository.findById(deployment.id().value())
				.map(existingEntity -> update(existingEntity, deployment))
				.orElseGet(() -> DeploymentJpaEntity.from(deployment));

		return repository.saveAndFlush(entity).toDomain();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Deployment> findById(DeploymentId deploymentId) {
		return repository.findById(deploymentId.value()).map(DeploymentJpaEntity::toDomain);
	}

	private static DeploymentJpaEntity update(
			DeploymentJpaEntity entity,
			Deployment deployment) {
		entity.updateFrom(deployment);
		return entity;
	}
}
