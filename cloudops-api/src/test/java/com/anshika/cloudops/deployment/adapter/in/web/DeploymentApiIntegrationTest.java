package com.anshika.cloudops.deployment.adapter.in.web;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class DeploymentApiIntegrationTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"));

	@Autowired
	private MockMvc mockMvc;

	@Test
	void submitsAndRetrievesDeployment() throws Exception {
		MvcResult creation = mockMvc.perform(post("/api/v1/deployments")
				.header("X-Correlation-ID", "api-integration-test")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "serviceName": "orders-api",
						  "image": "registry.example.com/orders:1.0.0",
						  "replicas": 2
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("X-Correlation-ID", "api-integration-test"))
				.andExpect(header().string("Location", matchesPattern(".*/api/v1/deployments/[0-9a-f-]{36}")))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.serviceName").value("orders-api"))
				.andExpect(jsonPath("$.image").value("registry.example.com/orders:1.0.0"))
				.andExpect(jsonPath("$.replicas").value(2))
				.andExpect(jsonPath("$.status").value("REQUESTED"))
				.andExpect(jsonPath("$.createdAt").isNotEmpty())
				.andExpect(jsonPath("$.updatedAt").isNotEmpty())
				.andReturn();

		String location = creation.getResponse().getHeader("Location");
		String deploymentId = location.substring(location.lastIndexOf('/') + 1);

		mockMvc.perform(get(URI.create(location).getPath()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(deploymentId))
				.andExpect(jsonPath("$.serviceName").value("orders-api"))
				.andExpect(jsonPath("$.status").value("REQUESTED"));
	}

	@Test
	void rejectsInvalidDeploymentWithFieldErrors() throws Exception {
		mockMvc.perform(post("/api/v1/deployments")
				.header("X-Correlation-ID", "validation-test")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "serviceName": " ",
						  "image": "",
						  "replicas": 0
						}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(header().string("X-Correlation-ID", "validation-test"))
				.andExpect(jsonPath("$.type").value(
						"https://cloudops.example/problems/request-validation-failed"))
				.andExpect(jsonPath("$.code").value("REQUEST_VALIDATION_FAILED"))
				.andExpect(jsonPath("$.correlationId").value("validation-test"))
				.andExpect(jsonPath("$.errors.serviceName").value("serviceName must not be blank"))
				.andExpect(jsonPath("$.errors.image").value("image must not be blank"))
				.andExpect(jsonPath("$.errors.replicas").value("replicas must be at least 1"));
	}

	@Test
	void returnsProblemDetailsForUnknownDeployment() throws Exception {
		mockMvc.perform(get(
				"/api/v1/deployments/{deploymentId}",
				"34580fda-b3f2-40d5-acd5-af22284488d7"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.type").value(
						"https://cloudops.example/problems/deployment-not-found"))
				.andExpect(jsonPath("$.code").value("DEPLOYMENT_NOT_FOUND"))
				.andExpect(jsonPath("$.correlationId").isNotEmpty());
	}

	@Test
	void rejectsMalformedDeploymentId() throws Exception {
		mockMvc.perform(get("/api/v1/deployments/not-a-uuid"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
	}
}
