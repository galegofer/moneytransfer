package com.tribalscale.test.controller

import com.tribalscale.test.domain.Account
import com.tribalscale.test.domain.MoneyTransfer
import com.tribalscale.test.domain.payload.AccountPayload
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload
import com.tribalscale.test.mapper.AccountMapper
import com.tribalscale.test.mapper.AccountMapperImpl
import com.tribalscale.test.mapper.MoneyTransferMapper
import com.tribalscale.test.mapper.MoneyTransferMapperImpl
import com.tribalscale.test.service.AccountTransferService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus.OK
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
internal class AccountControllerTest {
    @InjectMocks
    private val underTest: AccountController? = null

    @Mock
    private val accountTransferService: AccountTransferService? = null

    @Mock
    private val moneyTransferMapper: MoneyTransferMapper = MoneyTransferMapperImpl()

    @Mock
    private val accountMapper: AccountMapper = AccountMapperImpl()

    @Test
    fun transferFundsToAccount() {
        val payload =
            MoneyTransferRequestPayload(amount = 1000.0, sourceAccount = "1", targetAccount = "2", currency = "EUR")

        val moneyTransfer = MoneyTransfer(
            amount = payload.amount,
            sourceAccount = payload.sourceAccount,
            targetAccount = payload.targetAccount,
            currency = payload.currency
        )

        `when`(moneyTransferMapper.payloadToEntity(payload))
            .thenReturn(moneyTransfer)

        `when`(accountTransferService!!.transferMoneyFromAccountToAnotherAccount(moneyTransfer))
            .thenReturn(Mono.just(1))

        val result = underTest!!.transferFundsToAccount(payload).block()

        verify(accountTransferService).transferMoneyFromAccountToAnotherAccount(moneyTransfer)
        verify(moneyTransferMapper).payloadToEntity(payload)

        assertThat(result)
            .isNotNull
            .hasFieldOrPropertyWithValue("status", OK)
    }

    @Test
    fun accountDetails() {
        val accountId = "1"
        val account = Account(accountId = accountId, balance = 2000.0, currency = "EUR")

        val accountPayload = AccountPayload(accountId = account.accountId, balance = account.balance, currency = account.currency)

        `when`(accountMapper.entityToPayload(account))
            .thenReturn(accountPayload)

        `when`(accountTransferService!!.getAccountDetailsByAccountId(accountId))
            .thenReturn(Mono.just(account))
        val result = underTest!!.getAccountDetails("1")

        StepVerifier.create(result)
            .expectNextMatches { o: AccountPayload -> accountPayload == o }
            .verifyComplete()

        verify(accountTransferService).getAccountDetailsByAccountId(accountId)
        verify(accountMapper).entityToPayload(account)
    }
}