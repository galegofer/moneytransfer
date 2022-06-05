package com.tribalscale.test.domain.payload

import com.tribalscale.test.domain.validator.ValidCurrencyCode
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class MoneyTransferRequestPayload(
    @Schema(description = "The ISO code for the currency", name = "currency", required = true, example = "EUR")
    @ValidCurrencyCode
    @NotBlank @Pattern(regexp = "^[a-zA-Z\\s]*$")
    val currency: String,

    @Schema(
        description = "The amount to be transferred, non negative",
        name = "amount",
        required = true,
        example = "1000.0"
    )
    @Min(1)
    val amount: Double,

    @Schema(
        description = "The source account id (alphanumeric)",
        name = "sourceAccount",
        required = true,
        example = "11aa23"
    )
    @NotBlank @Pattern(regexp = "^[a-zA-Z\\d\\s]*$")
    val sourceAccount: String,

    @Schema(
        description = "The target account id (alphanumeric)",
        name = "sourceAccount",
        required = true,
        example = "11aa23"
    )
    @NotBlank @Pattern(regexp = "^[a-zA-Z\\d\\s]*$")
    val targetAccount: String
)