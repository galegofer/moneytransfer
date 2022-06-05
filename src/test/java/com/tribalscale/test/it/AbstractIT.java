package com.tribalscale.test.it;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.ClassRule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

abstract class AbstractIT {
    private static final int APPLICATION_PORT = 8080;
    private static final String APPLICATION_SERVICE_NAME = "application";
    private static final String HTTP = "http";

    @ClassRule
    public static final DockerComposeContainer composer =
            new DockerComposeContainer<>(new File("src/test/resources/compose-it.yml"))
                    .withExposedService(APPLICATION_SERVICE_NAME, APPLICATION_PORT,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));
                    //.withLocalCompose(true);

    static {
        composer.start();

        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = UriComponentsBuilder.newInstance()
                .host("localhost")
                .scheme(HTTP)
                .build()
                .toString();
        RestAssured.port = composer.getServicePort(APPLICATION_SERVICE_NAME, APPLICATION_PORT);
    }

    protected static byte[] readResource(String resourcePath) throws IOException {
        try (InputStream resource = new ClassPathResource(resourcePath).getInputStream()) {
            return ByteStreams.toByteArray(resource);
        }
    }
}
