package com.tribalscale.test.domain.payload;

import com.tribalscale.test.domain.validator.ValidCurrencyCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
@Builder
@Jacksonized
public class MoneyTransferRequestPayload {

    @ApiModelProperty(notes = "The ISO code for the currency", name = "currency", required = true, value = "EUR")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\s]*$")
    @ValidCurrencyCode
    String currency;

    @ApiModelProperty(notes = "The amount to be transferred, non negative", name = "amount", required = true, value = "1000.0")
    @Min(1)
    Double amount;

    @ApiModelProperty(notes = "The source account id (alphanumeric)", name = "sourceAccount", required = true, value = "11aa23")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$")
    String sourceAccount;

    @ApiModelProperty(notes = "The target account id (alphanumeric)", name = "sourceAccount", required = true, value = "11aa23")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$")
    String targetAccount;
}
