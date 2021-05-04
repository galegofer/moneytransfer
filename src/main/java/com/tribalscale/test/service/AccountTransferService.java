package com.tribalscale.test.service;

import com.tribalscale.test.domain.Account;
import com.tribalscale.test.domain.MoneyTransfer;
import reactor.core.publisher.Mono;

public interface AccountTransferService {
    Mono<Integer> transferMoneyFromAccountToAnotherAccount(MoneyTransfer moneyTransfer);

    Mono<Account> getAccountDetailsByAccountId(String accountId);
}
