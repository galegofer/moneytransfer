package com.tribalscale.test.service.impl;

import com.tribalscale.test.domain.Account;
import com.tribalscale.test.domain.MoneyTransfer;
import com.tribalscale.test.domain.MoneyTransferApplicationException;
import com.tribalscale.test.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class AccountTransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountTransferServiceImpl underTest;

    @Test
    void transferMoneyFromAccountToAnotherAccount() {
        var moneyTransfer = MoneyTransfer.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(2000d)
                .build();

        var sourceAccount = Account.builder()
                .accountId("1")
                .balance(3000d)
                .currency("EUR")
                .build();

        var targetAccount = Account.builder()
                .accountId("2")
                .balance(1000d)
                .currency("EUR")
                .build();

        when(accountRepository.getByAccountId(moneyTransfer.getSourceAccount()))
                .thenReturn(Mono.just(sourceAccount));

        when(accountRepository.getByAccountId(moneyTransfer.getTargetAccount()))
                .thenReturn(Mono.just(targetAccount));

        when(accountRepository.updateAmount(moneyTransfer.getSourceAccount(), sourceAccount.getBalance() - moneyTransfer.getAmount()))
                .thenReturn(Mono.just(1));

        when(accountRepository.updateAmount(moneyTransfer.getTargetAccount(), targetAccount.getBalance() + moneyTransfer.getAmount()))
                .thenReturn(Mono.just(2));

        Mono<Integer> result = underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(2))
                .verifyComplete();

        verify(accountRepository).getByAccountId(moneyTransfer.getSourceAccount());
        verify(accountRepository).getByAccountId(moneyTransfer.getTargetAccount());
        verify(accountRepository).updateAmount(moneyTransfer.getSourceAccount(), sourceAccount.getBalance() - moneyTransfer.getAmount());
        verify(accountRepository).updateAmount(moneyTransfer.getTargetAccount(), targetAccount.getBalance() + moneyTransfer.getAmount());
    }

    @Test
    void transferMoneyFromAccountToSourceAccount_nonExistingSourceAccount() {
        var moneyTransfer = MoneyTransfer.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(2000d)
                .build();

        var sourceAccount = Account.builder()
                .accountId("1")
                .balance(3000d)
                .currency("EUR")
                .build();

        var targetAccount = Account.builder()
                .accountId("2")
                .balance(1000d)
                .currency("EUR")
                .build();

        when(accountRepository.getByAccountId(moneyTransfer.getSourceAccount()))
                .thenReturn(Mono.empty());

        when(accountRepository.getByAccountId(moneyTransfer.getTargetAccount()))
                .thenReturn(Mono.just(targetAccount));

        var thrown = catchThrowable(() -> underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer).block());

        assertThat(thrown)
                .isInstanceOf(MoneyTransferApplicationException.class)
                .hasFieldOrPropertyWithValue("statusCode", NOT_FOUND.value());

        verify(accountRepository).getByAccountId(moneyTransfer.getSourceAccount());
        verify(accountRepository).getByAccountId(moneyTransfer.getTargetAccount());
        verify(accountRepository, never()).updateAmount(moneyTransfer.getSourceAccount(), sourceAccount.getBalance() - moneyTransfer.getAmount());
        verify(accountRepository, never()).updateAmount(moneyTransfer.getTargetAccount(), targetAccount.getBalance() + moneyTransfer.getAmount());
    }

    @Test
    void transferMoneyFromAccountToTargetAccount() {
        var moneyTransfer = MoneyTransfer.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(2000d)
                .build();

        var sourceAccount = Account.builder()
                .accountId("1")
                .balance(3000d)
                .currency("EUR")
                .build();

        var targetAccount = Account.builder()
                .accountId("2")
                .balance(1000d)
                .currency("EUR")
                .build();

        when(accountRepository.getByAccountId(moneyTransfer.getSourceAccount()))
                .thenReturn(Mono.just(sourceAccount));

        when(accountRepository.getByAccountId(moneyTransfer.getTargetAccount()))
                .thenReturn(Mono.empty());

        var thrown = catchThrowable(() -> underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer).block());

        assertThat(thrown)
                .isInstanceOf(MoneyTransferApplicationException.class)
                .hasFieldOrPropertyWithValue("statusCode", NOT_FOUND.value());

        verify(accountRepository).getByAccountId(moneyTransfer.getSourceAccount());
        verify(accountRepository).getByAccountId(moneyTransfer.getTargetAccount());
        verify(accountRepository, never()).updateAmount(moneyTransfer.getSourceAccount(), sourceAccount.getBalance() - moneyTransfer.getAmount());
        verify(accountRepository, never()).updateAmount(moneyTransfer.getTargetAccount(), targetAccount.getBalance() + moneyTransfer.getAmount());
    }

    @Test
    void transferMoneyFromAccountToAnotherAccountNoEnoughMoneySource() {
        var moneyTransfer = MoneyTransfer.builder()
                .sourceAccount("1")
                .targetAccount("2")
                .currency("EUR")
                .amount(9000d)
                .build();

        var sourceAccount = Account.builder()
                .accountId("1")
                .balance(1000d)
                .currency("EUR")
                .build();

        var targetAccount = Account.builder()
                .accountId("2")
                .balance(1000d)
                .currency("EUR")
                .build();

        when(accountRepository.getByAccountId(moneyTransfer.getSourceAccount()))
                .thenReturn(Mono.just(sourceAccount));

        when(accountRepository.getByAccountId(moneyTransfer.getTargetAccount()))
                .thenReturn(Mono.just(targetAccount));

        var thrown = catchThrowable(() -> underTest.transferMoneyFromAccountToAnotherAccount(moneyTransfer).block());

        assertThat(thrown)
                .isInstanceOf(MoneyTransferApplicationException.class)
                .hasFieldOrPropertyWithValue("statusCode", BAD_REQUEST.value());

        verify(accountRepository).getByAccountId(moneyTransfer.getSourceAccount());
        verify(accountRepository).getByAccountId(moneyTransfer.getTargetAccount());
        verify(accountRepository, never()).updateAmount(moneyTransfer.getSourceAccount(), sourceAccount.getBalance() - moneyTransfer.getAmount());
        verify(accountRepository, never()).updateAmount(moneyTransfer.getTargetAccount(), targetAccount.getBalance() + moneyTransfer.getAmount());
    }
}