package com.example.azurefunctiontc;

import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Import(TestContainersConfiguration.class)
@SpringBootTest
class UserAzureFunctionIT {

    @Autowired
    @Qualifier("function")
    private GenericContainer<?> function;

    @Test
    void shouldReturnUsers() {

        List<Customer> response = given()
                .when()
                .get(String.format("http://localhost:%s/api/users", function.getMappedPort(80)))
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(new TypeRef<>(){} );

        assertThat(response )
                .isNotNull()
                .isNotEmpty();
    }
}