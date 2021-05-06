package com.tribalscale.test.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload
import io.restassured.RestAssured
import io.restassured.http.ContentType.JSON
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import org.springframework.http.HttpStatus.*
import java.io.IOException

@Testable
internal class MoneyTransferApplicationIT : AbstractIT() {

    companion object {
        private val writer = ObjectMapper().writer()
    }

    @Test
    @Throws(IOException::class)
    fun transferFundsToAccount() {
        val requestPayload =
            MoneyTransferRequestPayload(sourceAccount = "1", targetAccount = "2", currency = "EUR", amount = 1000.0)

        RestAssured.given()
            .contentType(JSON)
            .`when`()
            .body(writer.writeValueAsBytes(requestPayload))
            .post("/account/transfer")
            .then()
            .statusCode(OK.value())
        RestAssured.given()
            .contentType(JSON)
            .`when`()["/account/1"]
            .then()
            .statusCode(OK.value())
            .body("accountId", equalTo("1"))
            .body("currency", equalTo("EUR"))
            .body("balance", equalTo(2000f))
        RestAssured.given()
            .contentType(JSON)
            .`when`()["/account/2"]
            .then()
            .statusCode(OK.value())
            .body("accountId", equalTo("2"))
            .body("currency", equalTo("EUR"))
            .body("balance", equalTo(1000f))
    }

    @Test
    @Throws(IOException::class)
    fun transferFundsToAccountInsufficientFundsSource() {
        val requestPayload =
            MoneyTransferRequestPayload(sourceAccount = "1", targetAccount = "2", currency = "EUR", amount = 10000.0)

        RestAssured.given()
            .contentType(JSON)
            .`when`()
            .body(writer.writeValueAsBytes(requestPayload))
            .post("/account/transfer")
            .then()
            .statusCode(BAD_REQUEST.value())
        RestAssured.given()
            .contentType(JSON)
            .`when`()["/account/1"]
            .then()
            .statusCode(OK.value())
            .body("accountId", equalTo("1"))
            .body("currency", equalTo("EUR"))
            .body("balance", equalTo(2000f))
        RestAssured.given()
            .contentType(JSON)
            .`when`()["/account/2"]
            .then()
            .statusCode(OK.value())
            .body("accountId", equalTo("2"))
            .body("currency", equalTo("EUR"))
            .body("balance", equalTo(1000f))
    }

    @Test
    @Throws(IOException::class)
    fun transferFundsToAccountNonExistingSource() {
        val requestPayload = MoneyTransferRequestPayload(
            sourceAccount = "nonexisting",
            targetAccount = "2",
            currency = "EUR",
            amount = 1000.0
        )

        RestAssured.given()
            .contentType(JSON)
            .`when`()
            .body(writer.writeValueAsBytes(requestPayload))
            .post("/account/transfer")
            .then()
            .statusCode(NOT_FOUND.value())
    }

    @Test
    @Throws(IOException::class)
    fun transferFundsToAccountNonExistingTarget() {
        val requestPayload = MoneyTransferRequestPayload(
            sourceAccount = "1",
            targetAccount = "nonexisting",
            currency = "EUR",
            amount = 1000.0
        )

        RestAssured.given()
            .contentType(JSON)
            .`when`()
            .body(writer.writeValueAsBytes(requestPayload))
            .post("/account/transfer")
            .then()
            .statusCode(NOT_FOUND.value())
    }
}