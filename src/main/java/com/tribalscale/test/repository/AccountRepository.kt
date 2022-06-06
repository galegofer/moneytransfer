package com.tribalscale.test.repository

import com.tribalscale.test.domain.Account
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountRepository : ReactiveCrudRepository<Account, String> {
    fun getByAccountId(id: String): Mono<Account>

    @Modifying
    @Query("update Account a set a.balance = :balance where a.account_id = :accountId")
    fun updateAmount(
        @Param(value = "accountId") accountId: String,
        @Param(value = "balance") balance: Double
    ): Mono<Int>
}
