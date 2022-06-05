package com.tribalscale.test.service.impl

import com.tribalscale.test.domain.Account
import com.tribalscale.test.domain.MoneyTransfer
import com.tribalscale.test.domain.MoneyTransferApplicationException
import com.tribalscale.test.repository.AccountRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
internal class AccountTransferServiceImplTest {
    @Mock
    private lateinit var accountRepository: AccountRepository

    @InjectMocks
    private lateinit var underTest: AccountTransferServiceImpl

    @Test
    fun transferMoneyFromAccountToAnotherAccount() {
        val moneyTransfer = MoneyTransfer(sourceAccount = "1", targetAccount = "2", currency = "EUR", amount = 2000.0)

        val sourceAccount = Account(accountId = "1", balance = 3000.0, currency = "EUR")
        val targetAccount = Account(accountId = "2", balance = 1000.0, currency = "EUR")

        `when`(accountRepository.getByAccountId(moneyTransfer.sourceAccount))
            .thenReturn(Mono.just(sourceAccount))
        `when`(accountRepository.getByAccountId(moneyTransfer.targetAccount))
            .thenReturn(Mono.just(targetAccount))
        `when`(
            accountRepository.updateAmount(
                moneyTransfer.sourceAccount,
                sourceAccount.balance - moneyTransfer.amount
            )
        )
            .thenReturn(Mono.just(1))
        `when`(
            accountRepository.updateAmount(
                moneyTransfer.targetAccount,
                targetAccount.balance + moneyTransfer.amount
            )
        )
            .thenReturn(Mono.just(2))
        val result = underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer)
        StepVerifier.create(result)
            .expectNextMatches { value: Int -> value == 2 }
            .verifyComplete()

        verify(accountRepository).getByAccountId(moneyTransfer.sourceAccount)
        verify(accountRepository).getByAccountId(moneyTransfer.targetAccount)
        verify(accountRepository)
            .updateAmount(moneyTransfer.sourceAccount, sourceAccount.balance - moneyTransfer.amount)
        verify(accountRepository)
            .updateAmount(moneyTransfer.targetAccount, targetAccount.balance + moneyTransfer.amount)
    }

    @Test
    fun transferMoneyFromAccountToSourceAccount_nonExistingSourceAccount() {
        val moneyTransfer = MoneyTransfer(sourceAccount = "1", targetAccount = "2", currency = "EUR", amount = 2000.0)

        val sourceAccount = Account(accountId = "1", balance = 3000.0, currency = "EUR")
        val targetAccount = Account(accountId = "2", balance = 1000.0, currency = "EUR")

        `when`(accountRepository.getByAccountId(moneyTransfer.sourceAccount))
            .thenReturn(Mono.empty())
        `when`(accountRepository.getByAccountId(moneyTransfer.targetAccount))
            .thenReturn(Mono.just(targetAccount))
        val thrown =
            catchThrowable { underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer).block() }
        assertThat(thrown)
            .isInstanceOf(MoneyTransferApplicationException::class.java)
            .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND.value())
        verify(accountRepository).getByAccountId(moneyTransfer.sourceAccount)
        verify(accountRepository).getByAccountId(moneyTransfer.targetAccount)
        verify(accountRepository, Mockito.never())
            .updateAmount(moneyTransfer.sourceAccount, sourceAccount.balance - moneyTransfer.amount)
        verify(accountRepository, Mockito.never())
            .updateAmount(moneyTransfer.targetAccount, targetAccount.balance + moneyTransfer.amount)
    }

    @Test
    fun transferMoneyFromAccountToTargetAccount() {
        val moneyTransfer = MoneyTransfer(sourceAccount = "1", targetAccount = "2", currency = "EUR", amount = 2000.0)

        val sourceAccount = Account(accountId = "1", balance = 3000.0, currency = "EUR")
        val targetAccount = Account(accountId = "2", balance = 1000.0, currency = "EUR")

        `when`(accountRepository.getByAccountId(moneyTransfer.sourceAccount))
            .thenReturn(Mono.just(sourceAccount))
        `when`(accountRepository.getByAccountId(moneyTransfer.targetAccount))
            .thenReturn(Mono.empty())

        val thrown =
            catchThrowable { underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer).block() }

        assertThat(thrown)
            .isInstanceOf(MoneyTransferApplicationException::class.java)
            .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND.value())

        verify(accountRepository).getByAccountId(moneyTransfer.sourceAccount)
        verify(accountRepository).getByAccountId(moneyTransfer.targetAccount)
        verify(accountRepository, Mockito.never())
            .updateAmount(moneyTransfer.sourceAccount, sourceAccount.balance - moneyTransfer.amount)
        verify(accountRepository, Mockito.never())
            .updateAmount(moneyTransfer.targetAccount, targetAccount.balance + moneyTransfer.amount)
    }

    @Test
    fun transferMoneyFromAccountToAnotherAccountNoEnoughMoneySource() {
        val moneyTransfer = MoneyTransfer(sourceAccount = "1", targetAccount = "2", currency = "EUR", amount = 9000.0)
        val sourceAccount = Account(accountId = "1", balance = 1000.0, currency = "EUR")
        val targetAccount = Account(accountId = "2", balance = 1000.0, currency = "EUR")

        `when`(accountRepository.getByAccountId(moneyTransfer.sourceAccount))
            .thenReturn(Mono.just(sourceAccount))
        `when`(accountRepository.getByAccountId(moneyTransfer.targetAccount))
            .thenReturn(Mono.just(targetAccount))
        val thrown =
            catchThrowable { underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer).block() }

        assertThat(thrown)
            .isInstanceOf(MoneyTransferApplicationException::class.java)
            .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST.value())

        verify(accountRepository).getByAccountId(moneyTransfer.sourceAccount)
        verify(accountRepository).getByAccountId(moneyTransfer.targetAccount)
        verify(accountRepository, Mockito.never())
            .updateAmount(moneyTransfer.sourceAccount, sourceAccount.balance - moneyTransfer.amount)
        verify(accountRepository, Mockito.never())
            .updateAmount(moneyTransfer.targetAccount, targetAccount.balance + moneyTransfer.amount)
    }
}