package com.tribalscale.test.domain.payload

import io.swagger.annotations.ApiModelProperty
import lombok.Builder
import lombok.Value
import lombok.extern.jackson.Jacksonized
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Value
@Builder
@Jacksonized
class AccountPayload(
    @ApiModelProperty(notes = "The account id (alphanumeric)", name = "accountId", required = true, value = "11aa23")
    val accountId: @NotBlank @Pattern(regexp = "^[a-zA-Z0-9\\s]*$") String,

    @ApiModelProperty(notes = "The ISO code for the currency", name = "currency", required = true, value = "EUR")
    val currency: @NotBlank @Pattern(regexp = "^[a-zA-Z\\s]*$") String,

    @ApiModelProperty(notes = "The final balance for this account", name = "balance", required = true, value = "1000.0")
    val balance: Double
)