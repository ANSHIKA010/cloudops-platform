package com.anshika.cloudops.deployment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class DeploymentSpecTest {

	@Test
	void normalizesTextFields() {
		DeploymentSpec specification = new DeploymentSpec("  orders-api  ", "  orders:1.0  ", 1);

		assertThat(specification.serviceName()).isEqualTo("orders-api");
		assertThat(specification.image()).isEqualTo("orders:1.0");
	}

	@Test
	void rejectsBlankServiceName() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DeploymentSpec("  ", "orders:1.0", 1))
				.withMessage("serviceName must not be blank");
	}

	@Test
	void rejectsBlankImage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DeploymentSpec("orders-api", null, 1))
				.withMessage("image must not be blank");
	}

	@Test
	void rejectsServiceNameLongerThanOneHundredCharacters() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DeploymentSpec("a".repeat(101), "orders:1.0", 1))
				.withMessage("serviceName must not exceed 100 characters");
	}

	@Test
	void rejectsImageLongerThanTwoHundredFiftyFiveCharacters() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DeploymentSpec("orders-api", "a".repeat(256), 1))
				.withMessage("image must not exceed 255 characters");
	}

	@Test
	void rejectsReplicaCountBelowMinimum() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DeploymentSpec("orders-api", "orders:1.0", 0))
				.withMessage("replicas must be between 1 and 5");
	}

	@Test
	void rejectsReplicaCountAboveMaximum() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DeploymentSpec("orders-api", "orders:1.0", 6))
				.withMessage("replicas must be between 1 and 5");
	}
}
