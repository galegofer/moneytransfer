package com.tribalscale.test.controller

import com.tribalscale.test.domain.Account
import com.tribalscale.test.domain.payload.AccountPayload
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload
import com.tribalscale.test.mapper.AccountMapper
import com.tribalscale.test.mapper.MoneyTransferMapper
import com.tribalscale.test.service.AccountTransferService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Tag(
    name = "account-controller",
    description = "Endpoints for moving money from account to account, and getting account details"
)
@RestController
@RequestMapping("/account")
@Validated
class AccountController(
    private val accountTransferService: AccountTransferService,
    private val moneyTransferMapper: MoneyTransferMapper,
    private val accountMapper: AccountMapper
) {
    private val log = KotlinLogging.logger {}

    @Operation(description = "Creates a transfer between the source and target account with the given amount.")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "The transfer between the source account and the target was made"
        ),
        ApiResponse(
            responseCode = "400",
            description = "The source account doesn't has enough funds as specified in the amount, or either invalid input parameters",
        ),
        ApiResponse(
            responseCode = "404",
            description = "Either the source or the target couldn't be found.",
        ),
        ApiResponse(responseCode = "500", description = "An internal server happened")
    )
    @PostMapping("/transfer")
    fun transferFundsToAccount(@RequestBody payload: @Valid MoneyTransferRequestPayload): Mono<ResponseEntity<Void>> =
        log.info(
            "Received request to make transfer from account id: {} to account id: {} for amount: {}",
            payload.sourceAccount, payload.targetAccount, payload.amount
        ).let {
            accountTransferService.transferMoneyFromAccountToAnotherAccount(
                moneyTransferMapper.payloadToEntity(payload)
            ).map {
                ResponseEntity.status(OK)
                    .build()
            }
        }

    @Operation(description = "Get the details for the account with the given account id.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "The account details for the given account id."),
        ApiResponse(responseCode = "400", description = "The account id contains a wrong format."),
        ApiResponse(responseCode = "404", description = "The provided account id doesn't exist."),
        ApiResponse(responseCode = "500", description = "An internal server happened")
    )
    @GetMapping("/{id}")
    fun getAccountDetails(@PathVariable("id") id: @NotBlank @Size(max = 100) @Pattern(regexp = "^[a-zA-Z\\d\\s]*$") String): Mono<AccountPayload> =
        log.info("Received request to get details for account id: {}", id)
            .let {
                accountTransferService.getAccountDetailsByAccountId(id)
                    .map { entity: Account -> accountMapper.entityToPayload(entity) }
            }
}