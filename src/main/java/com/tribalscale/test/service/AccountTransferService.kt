package com.tribalscale.test.service

import com.tribalscale.test.domain.Account
import com.tribalscale.test.domain.MoneyTransfer
import reactor.core.publisher.Mono

interface AccountTransferService {
    fun transferMoneyFromAccountToAnotherAccount(moneyTransfer: MoneyTransfer): Mono<Int>
    fun getAccountDetailsByAccountId(accountId: String): Mono<Account>
}