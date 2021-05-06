package com.tribalscale.test.service.impl

import com.tribalscale.test.domain.Account
import com.tribalscale.test.domain.MoneyTransfer
import com.tribalscale.test.domain.MoneyTransferApplicationException
import com.tribalscale.test.repository.AccountRepository
import com.tribalscale.test.service.AccountTransferService
import mu.KotlinLogging
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.function.BiFunction
import java.util.function.Function

@Service
open class AccountTransferServiceImpl(private val accountRepository: AccountRepository) : AccountTransferService {

    private val log = KotlinLogging.logger {}

    @Transactional
    override fun transferMoneyFromAccountToAnotherAccount(moneyTransfer: MoneyTransfer): Mono<Int> {
        log.info(
            "Calling transfer money from account id: {} to account id: {} for amount: {}",
            moneyTransfer.sourceAccount, moneyTransfer.targetAccount, moneyTransfer.amount
        )

        return accountRepository.getByAccountId(moneyTransfer.sourceAccount)
            .switchIfEmpty(
                Mono.error(
                    MoneyTransferApplicationException(
                        message = String.format(
                            "Source account with id: %s, doesn't exist",
                            moneyTransfer.sourceAccount
                        ), statusCode = NOT_FOUND.value()
                    )
                )
            )
            .filter { sourceAccount: Account -> sourceAccount.balance >= moneyTransfer.amount }
            .switchIfEmpty(
                Mono.error(
                    MoneyTransferApplicationException(
                        message = String.format(
                            "Insufficient funds at Source account with id: %s",
                            moneyTransfer.sourceAccount
                        ), statusCode = BAD_REQUEST.value()
                    )
                )
            )
            .doOnSuccess { sourceAccount: Account ->
                log.info(
                    "Got source account with id: {} and balance: {}",
                    sourceAccount.accountId,
                    sourceAccount.balance
                )
            }
            .zipWith(
                accountRepository.getByAccountId(moneyTransfer.targetAccount)
                    .switchIfEmpty(
                        Mono.error(
                            MoneyTransferApplicationException(
                                message = String.format(
                                    "Target account with id: %s, doesn't exist",
                                    moneyTransfer.targetAccount
                                ), statusCode = NOT_FOUND.value()
                            )
                        )
                    )
                    .doOnSuccess { targetAccount: Account ->
                        log.info(
                            "Got target account with id: {} and balance: {}",
                            targetAccount.accountId,
                            targetAccount.balance
                        )
                    },
                applyDiscountToSourceAccountAndAddToTarget(moneyTransfer)
            )
            .doOnSuccess { log.info("Updated all balances...") }
            .flatMap(Function.identity())
    }

    override fun getAccountDetailsByAccountId(accountId: String): Mono<Account> {
        return accountRepository.getByAccountId(accountId)
            .switchIfEmpty(
                Mono.error(
                    MoneyTransferApplicationException(
                        message = String.format("Account with id: %s, doesn't exist", accountId),
                        statusCode = NOT_FOUND.value()
                    )
                )
            )
    }

    private fun applyDiscountToSourceAccountAndAddToTarget(moneyTransfer: MoneyTransfer): BiFunction<Account, Account, Mono<Int>> {
        return BiFunction { sourceAccount: Account, targetAccount: Account ->
            accountRepository.updateAmount(sourceAccount.accountId, sourceAccount.balance - moneyTransfer.amount)
                .doOnSuccess {
                    log.info(
                        "Subtracted amount: {} for source account id: {} and balance: {}",
                        moneyTransfer.amount,
                        sourceAccount.accountId,
                        sourceAccount.balance
                    )
                }
                .flatMap {
                    accountRepository.updateAmount(
                        targetAccount.accountId,
                        targetAccount.balance + moneyTransfer.amount
                    )
                        .doOnSuccess {
                            log.info(
                                "Added amount: {} for target account id: {}",
                                moneyTransfer.amount,
                                moneyTransfer.targetAccount
                            )
                        }
                }
        }
    }
}