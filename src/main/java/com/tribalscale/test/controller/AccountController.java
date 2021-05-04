package com.tribalscale.test.controller;

import com.tribalscale.test.domain.payload.AccountPayload;
import com.tribalscale.test.domain.payload.ErrorPayload;
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload;
import com.tribalscale.test.mapper.AccountMapper;
import com.tribalscale.test.mapper.MoneyTransferMapper;
import com.tribalscale.test.service.AccountTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.springframework.http.HttpStatus.OK;

@Api(description = "Endpoints for moving money from account to account, and getting account details", tags = {"money-transfer"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Validated
@Slf4j
public class AccountController {

    private final AccountTransferService accountTransferService;
    private final MoneyTransferMapper moneyTransferMapper;
    private final AccountMapper accountMapper;

    @ApiOperation(value = "Creates a transfer between the source and target account with the given amount.")
    @ApiResponses({@ApiResponse(code = 200, message = "The transfer between the source account and the target was made"),
            @ApiResponse(code = 400, message = "The source account doesn't has enough funds as specified in the amount, or either invalid input parameters", response = ErrorPayload.class),
            @ApiResponse(code = 404, message = "Either the source or the target couldn't be found.", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "An internal server happened", response = ErrorPayload.class)})
    @PostMapping("/transfer")
    public Mono<ResponseEntity<Void>> transferFundsToAccount(@Valid @RequestBody MoneyTransferRequestPayload payload) {
        log.info("Received request to make transfer from account id: {} to account id: {} for amount: {}",
                payload.getSourceAccount(), payload.getTargetAccount(), payload.getAmount());

        return accountTransferService.transferMoneyFromAccountToAnotherAccount(moneyTransferMapper.payloadToEntity(payload))
                .map(moneyTransfer -> ResponseEntity.status(OK)
                        .build());
    }

    @ApiOperation(value = "Get the details for the account with the given account id.")
    @ApiResponses({@ApiResponse(code = 200, message = "The account details for the given account id."),
            @ApiResponse(code = 400, message = "The account id contains a wrong format.", response = ErrorPayload.class),
            @ApiResponse(code = 404, message = "The provided account id doesn't exist.", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "An internal server happened", response = ErrorPayload.class)})
    @GetMapping("/{id}")
    public Mono<AccountPayload> getAccountDetails(@PathVariable("id") @NotBlank @Size(max = 100) @Pattern(regexp = "^[a-zA-Z0-9\\s]*$") String id) {
        log.info("Received request to get details for account id: {}", id);

        return accountTransferService.getAccountDetailsByAccountId(id)
                .map(accountMapper::entityToPayload);
    }
}
