package com.anshika.cloudops.deployment.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.anshika.cloudops.deployment.application.port.out.DeploymentRepository;
import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;
import com.anshika.cloudops.deployment.domain.DeploymentSpec;
import com.anshika.cloudops.deployment.domain.DeploymentStatus;

@Testcontainers
@DataJpaTest
@Import(JpaDeploymentRepositoryAdapterTest.RepositoryTestConfiguration.class)
class JpaDeploymentRepositoryAdapterTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"));

	private static final DeploymentId DEPLOYMENT_ID =
			new DeploymentId(UUID.fromString("85ad42a0-ab2d-4300-b2a2-da54f018e80c"));
	private static final Instant REQUESTED_AT = Instant.parse("2026-09-27T08:30:00.123456Z");

	@Autowired
	private DeploymentRepository repository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void savesAndLoadsDeploymentWithoutLosingDomainData() {
		Deployment deployment = Deployment.request(
				DEPLOYMENT_ID,
				new DeploymentSpec("orders-api", "registry.example.com/orders:1.0.0", 3),
				REQUESTED_AT);

		repository.save(deployment);

		Deployment loaded = repository.findById(DEPLOYMENT_ID).orElseThrow();
		assertThat(loaded.id()).isEqualTo(DEPLOYMENT_ID);
		assertThat(loaded.specification()).isEqualTo(deployment.specification());
		assertThat(loaded.status()).isEqualTo(DeploymentStatus.REQUESTED);
		assertThat(loaded.createdAt()).isEqualTo(REQUESTED_AT);
		assertThat(loaded.updatedAt()).isEqualTo(REQUESTED_AT);
	}

	@Test
	void updatesLifecycleStateUsingOptimisticVersioning() {
		Deployment deployment = Deployment.request(
				DEPLOYMENT_ID,
				new DeploymentSpec("orders-api", "orders:1.0.0", 2),
				REQUESTED_AT);
		repository.save(deployment);

		deployment.startDeployment(REQUESTED_AT.plusSeconds(10));
		repository.save(deployment);

		Deployment loaded = repository.findById(DEPLOYMENT_ID).orElseThrow();
		assertThat(loaded.status()).isEqualTo(DeploymentStatus.DEPLOYING);
		assertThat(loaded.updatedAt()).isEqualTo(REQUESTED_AT.plusSeconds(10));
		assertThat(persistedVersion()).isEqualTo(1L);
	}

	@Test
	void returnsEmptyWhenDeploymentDoesNotExist() {
		DeploymentId unknownId = new DeploymentId(
				UUID.fromString("10730458-51fb-4fd6-af13-ec1b51f5bf48"));

		assertThat(repository.findById(unknownId)).isEmpty();
	}

	private Long persistedVersion() {
		return jdbcTemplate.queryForObject(
				"SELECT version FROM deployments WHERE id = ?",
				Long.class,
				DEPLOYMENT_ID.value());
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class RepositoryTestConfiguration {

		@Bean
		DeploymentRepository deploymentRepository(SpringDataDeploymentRepository repository) {
			return new JpaDeploymentRepositoryAdapter(repository);
		}
	}
}
