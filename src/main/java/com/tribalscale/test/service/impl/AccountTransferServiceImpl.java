package com.tribalscale.test.service.impl;

import com.tribalscale.test.domain.Account;
import com.tribalscale.test.domain.MoneyTransfer;
import com.tribalscale.test.domain.MoneyTransferApplicationException;
import com.tribalscale.test.repository.AccountRepository;
import com.tribalscale.test.service.AccountTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountTransferServiceImpl implements AccountTransferService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Mono<Integer> transferMoneyFromAccountToAnotherAccount(MoneyTransfer moneyTransfer) {
        log.info("Calling transfer money from account id: {} to account id: {} for amount: {}",
                moneyTransfer.getSourceAccount(), moneyTransfer.getTargetAccount(), moneyTransfer.getAmount());

        return accountRepository.getByAccountId(moneyTransfer.getSourceAccount())
                .switchIfEmpty(Mono.error(MoneyTransferApplicationException.builder()
                        .message(format("Source account with id: %s, doesn't exist", moneyTransfer.getSourceAccount()))
                        .statusCode(NOT_FOUND.value())
                        .build()))
                .filter(sourceAccount -> sourceAccount.getBalance() >= moneyTransfer.getAmount())
                .switchIfEmpty(Mono.error(MoneyTransferApplicationException.builder()
                        .message(format("Insufficient funds at Source account with id: %s", moneyTransfer.getSourceAccount()))
                        .statusCode(BAD_REQUEST.value())
                        .build()))
                .doOnSuccess(sourceAccount -> log.info("Got source account with id: {} and balance: {}", sourceAccount.getAccountId(), sourceAccount.getBalance()))
                .zipWith(accountRepository.getByAccountId(moneyTransfer.getTargetAccount())
                                .switchIfEmpty(Mono.error(MoneyTransferApplicationException.builder()
                                        .message(format("Target account with id: %s, doesn't exist", moneyTransfer.getTargetAccount()))
                                        .statusCode(NOT_FOUND.value())
                                        .build()))
                                .doOnSuccess(targetAccount -> log.info("Got target account with id: {} and balance: {}", targetAccount.getAccountId(), targetAccount.getBalance())),
                        applyDiscountToSourceAccountAndAddToTarget(moneyTransfer))
                .doOnSuccess(accountMono -> log.info("Updated all balances..."))
                .flatMap(Function.identity());
    }

    @Override
    public Mono<Account> getAccountDetailsByAccountId(String accountId) {
        return accountRepository.getByAccountId(accountId)
                .switchIfEmpty(Mono.error(MoneyTransferApplicationException.builder()
                        .message(format("Account with id: %s, doesn't exist", accountId))
                        .statusCode(NOT_FOUND.value())
                        .build()));
    }

    private BiFunction<Account, Account, Mono<Integer>> applyDiscountToSourceAccountAndAddToTarget(MoneyTransfer moneyTransfer) {
        return (sourceAccount, targetAccount) -> accountRepository.updateAmount(sourceAccount.getAccountId(), sourceAccount.getBalance() - moneyTransfer.getAmount())
                .doOnSuccess(account -> log.info("Subtracted amount: {} for source account id: {} and balance: {}", moneyTransfer.getAmount(), sourceAccount.getAccountId(), sourceAccount.getBalance()))
                .flatMap(result -> accountRepository.updateAmount(targetAccount.getAccountId(), targetAccount.getBalance() + moneyTransfer.getAmount())
                        .doOnSuccess(account -> log.info("Added amount: {} for target account id: {}", moneyTransfer.getAmount(), moneyTransfer.getTargetAccount())));
    }
}
