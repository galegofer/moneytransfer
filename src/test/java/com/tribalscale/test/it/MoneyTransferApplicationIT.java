package com.tribalscale.test.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.io.IOException;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.HttpStatus.*;

@Testable
class MoneyTransferApplicationIT extends AbstractIT {

    private static final ObjectWriter writer = new ObjectMapper().writer();

    @Test
    void transferFundsToAccount() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(OK.value());

        RestAssured.given()
                .contentType(JSON)
                .when()
                .get("/account/1")
                .then()
                .statusCode(OK.value())
                .body("accountId", equalTo("1"))
                .body("currency", equalTo("EUR"))
                .body("balance", equalTo(2000f));

        RestAssured.given()
                .contentType(JSON)
                .when()
                .get("/account/2")
                .then()
                .statusCode(OK.value())
                .body("accountId", equalTo("2"))
                .body("currency", equalTo("EUR"))
                .body("balance", equalTo(1000f));
    }

    @Test
    void transferFundsToAccountInsufficientFundsSource() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(10000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(BAD_REQUEST.value());

        RestAssured.given()
                .contentType(JSON)
                .when()
                .get("/account/1")
                .then()
                .statusCode(OK.value())
                .body("accountId", equalTo("1"))
                .body("currency", equalTo("EUR"))
                .body("balance", equalTo(2000f));

        RestAssured.given()
                .contentType(JSON)
                .when()
                .get("/account/2")
                .then()
                .statusCode(OK.value())
                .body("accountId", equalTo("2"))
                .body("currency", equalTo("EUR"))
                .body("balance", equalTo(1000f));
    }

    @Test
    void transferFundsToAccountNonExistingSource() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("nonexisting")
                .targetAccount("2")
                .currency("EUR")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void transferFundsToAccountNonExistingTarget() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .targetAccount("nonexisting")
                .currency("EUR")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void transferFundsToAccountWrongCurrency() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("invalidCurrency")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void transferFundsToAccountInvalidAmount() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(-1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void transferFundsToAccountSourceNotProvided() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .targetAccount("2")
                .currency("EUR")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void transferFundsToAccountTargetNotProvided() throws IOException {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .currency("EUR")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .body(writer.writeValueAsBytes(requestPayload))
                .post("/account/transfer")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void getAccountDetails() {
        MoneyTransferRequestPayload requestPayload = MoneyTransferRequestPayload.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(1000d)
                .build();

        RestAssured.given()
                .contentType(JSON)
                .when()
                .get("/account/1")
                .then()
                .statusCode(OK.value())
                .body("accountId", equalTo("1"))
                .body("currency", equalTo("EUR"))
                .body("balance", equalTo(2000f));
    }
}
