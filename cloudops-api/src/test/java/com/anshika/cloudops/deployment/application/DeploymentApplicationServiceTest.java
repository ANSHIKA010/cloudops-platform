package com.anshika.cloudops.deployment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.anshika.cloudops.deployment.application.port.in.SubmitDeploymentCommand;
import com.anshika.cloudops.deployment.application.port.out.DeploymentRepository;
import com.anshika.cloudops.deployment.domain.Deployment;
import com.anshika.cloudops.deployment.domain.DeploymentId;
import com.anshika.cloudops.deployment.domain.DeploymentStatus;

class DeploymentApplicationServiceTest {

	private static final DeploymentId GENERATED_ID =
			new DeploymentId(UUID.fromString("9cff1b54-6bbc-4db8-86c8-655f744948b8"));
	private static final Instant NOW = Instant.parse("2026-09-27T10:00:00.123456789Z");
	private static final Instant PERSISTED_NOW = Instant.parse("2026-09-27T10:00:00.123456Z");

	private final InMemoryDeploymentRepository repository = new InMemoryDeploymentRepository();
	private final DeploymentApplicationService service = new DeploymentApplicationService(
			repository,
			() -> GENERATED_ID,
			Clock.fixed(NOW, ZoneOffset.UTC));

	@Test
	void submitsValidatedDeploymentUsingInjectedIdAndClock() {
		Deployment deployment = service.submit(
				new SubmitDeploymentCommand(" orders-api ", " orders:1.0.0 ", 2));

		assertThat(deployment.id()).isEqualTo(GENERATED_ID);
		assertThat(deployment.specification().serviceName()).isEqualTo("orders-api");
		assertThat(deployment.specification().image()).isEqualTo("orders:1.0.0");
		assertThat(deployment.specification().replicas()).isEqualTo(2);
		assertThat(deployment.status()).isEqualTo(DeploymentStatus.REQUESTED);
		assertThat(deployment.createdAt()).isEqualTo(PERSISTED_NOW);
		assertThat(repository.findById(GENERATED_ID)).containsSame(deployment);
	}

	@Test
	void retrievesExistingDeployment() {
		Deployment submitted = service.submit(
				new SubmitDeploymentCommand("orders-api", "orders:1.0.0", 1));

		assertThat(service.get(GENERATED_ID)).isSameAs(submitted);
	}

	@Test
	void rejectsUnknownDeploymentId() {
		assertThatThrownBy(() -> service.get(GENERATED_ID))
				.isInstanceOf(DeploymentNotFoundException.class)
				.hasMessage("Deployment 9cff1b54-6bbc-4db8-86c8-655f744948b8 was not found");
	}

	private static final class InMemoryDeploymentRepository implements DeploymentRepository {

		private final Map<DeploymentId, Deployment> deployments = new HashMap<>();

		@Override
		public Deployment save(Deployment deployment) {
			deployments.put(deployment.id(), deployment);
			return deployment;
		}

		@Override
		public Optional<Deployment> findById(DeploymentId deploymentId) {
			return Optional.ofNullable(deployments.get(deploymentId));
		}
	}
}
