package com.tribalscale.test.repository;

import com.tribalscale.test.domain.Account;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    Mono<Account> getByAccountId(String id);

    @Modifying
    @Query("update Account a set a.balance = :balance where a.account_id = :accountId")
    Mono<Integer> updateAmount(@Param(value = "accountId") String accountId, @Param(value = "balance") Double balance);
}
