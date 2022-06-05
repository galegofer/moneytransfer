package com.tribalscale.test.domain.payload

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Builder
import lombok.Value
import lombok.extern.jackson.Jacksonized
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Value
@Builder
@Jacksonized
class AccountPayload(
    @Schema(description = "The account id (alphanumeric)", name = "accountId", required = true, example = "11aa23")
    val accountId: @NotBlank @Pattern(regexp = "^[a-zA-Z\\d\\s]*$") String,

    @Schema(description = "The ISO code for the currency", name = "currency", required = true, example = "EUR")
    val currency: @NotBlank @Pattern(regexp = "^[a-zA-Z\\s]*$") String,

    @Schema(description = "The final balance for this account", name = "balance", required = true, example = "1000.0")
    val balance: Double
)