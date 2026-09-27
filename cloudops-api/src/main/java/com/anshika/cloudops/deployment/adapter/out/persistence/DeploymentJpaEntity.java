package com.anshika.cloudops.deployment.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;
import com.anshika.cloudops.deployment.domain.DeploymentSpec;
import com.anshika.cloudops.deployment.domain.DeploymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "deployments")
class DeploymentJpaEntity {

	@Id
	private UUID id;

	@Column(name = "service_name", nullable = false, length = DeploymentSpec.MAX_SERVICE_NAME_LENGTH)
	private String serviceName;

	@Column(name = "image_reference", nullable = false, length = DeploymentSpec.MAX_IMAGE_LENGTH)
	private String image;

	@Column(nullable = false)
	private int replicas;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private DeploymentStatus status;

	@Version
	@Column(nullable = false)
	private long version;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected DeploymentJpaEntity() {
	}

	private DeploymentJpaEntity(Deployment deployment) {
		this.id = deployment.id().value();
		copyMutableState(deployment);
		this.createdAt = deployment.createdAt();
	}

	static DeploymentJpaEntity from(Deployment deployment) {
		return new DeploymentJpaEntity(deployment);
	}

	void updateFrom(Deployment deployment) {
		if (!id.equals(deployment.id().value())) {
			throw new IllegalArgumentException("deployment ID cannot be changed");
		}
		if (!createdAt.equals(deployment.createdAt())) {
			throw new IllegalArgumentException("deployment creation time cannot be changed");
		}
		copyMutableState(deployment);
	}

	private void copyMutableState(Deployment deployment) {
		DeploymentSpec specification = deployment.specification();
		this.serviceName = specification.serviceName();
		this.image = specification.image();
		this.replicas = specification.replicas();
		this.status = deployment.status();
		this.updatedAt = deployment.updatedAt();
	}

	Deployment toDomain() {
		return Deployment.rehydrate(
				new DeploymentId(id),
				new DeploymentSpec(serviceName, image, replicas),
				status,
				createdAt,
				updatedAt);
	}
}
