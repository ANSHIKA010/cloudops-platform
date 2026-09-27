package com.anshika.cloudops.deployment.application.port.in;

public record SubmitDeploymentCommand(String serviceName, String image, int replicas) {
}
