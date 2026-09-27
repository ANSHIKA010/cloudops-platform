package com.anshika.cloudops.deployment.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataDeploymentRepository extends JpaRepository<DeploymentJpaEntity, UUID> {
}
