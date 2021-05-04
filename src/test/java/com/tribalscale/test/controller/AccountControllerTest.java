package com.tribalscale.test.controller;

import com.tribalscale.test.domain.Account;
import com.tribalscale.test.domain.payload.AccountPayload;
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload;
import com.tribalscale.test.mapper.AccountMapper;
import com.tribalscale.test.mapper.AccountMapperImpl;
import com.tribalscale.test.mapper.MoneyTransferMapper;
import com.tribalscale.test.mapper.MoneyTransferMapperImpl;
import com.tribalscale.test.service.AccountTransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController underTest;

    @Mock
    private AccountTransferService accountTransferService;

    @Spy
    private final MoneyTransferMapper moneyTransferMapper = new MoneyTransferMapperImpl();

    @Spy
    private final AccountMapper accountMapper = new AccountMapperImpl();

    @Test
    void transferFundsToAccount() {
        var payload = MoneyTransferRequestPayload.builder()
                .amount(1000d)
                .sourceAccount("1")
                .targetAccount("2")
                .build();

        var moneyTransfer = moneyTransferMapper.payloadToEntity(payload);

        when(accountTransferService.transferMoneyFromAccountToAnotherAccount(moneyTransfer))
                .thenReturn(Mono.just(1));

        ResponseEntity<Void> result = underTest.transferFundsToAccount(payload).block();

        verify(accountTransferService).transferMoneyFromAccountToAnotherAccount(moneyTransfer);
        verify(moneyTransferMapper, times(2)).payloadToEntity(payload);

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("status", OK);
    }

    @Test
    void getAccountDetails() {
        var accountId = "1";

        Account account = Account.builder()
                .accountId(accountId)
                .balance(2000d)
                .currency("EUR")
                .build();

        var accountPayload = accountMapper.entityToPayload(account);

        when(accountTransferService.getAccountDetailsByAccountId(accountId))
                .thenReturn(Mono.just(account));

        Mono<AccountPayload> result = underTest.getAccountDetails("1");

        StepVerifier.create(result)
                .expectNextMatches(accountPayload::equals)
                .verifyComplete();

        verify(accountTransferService).getAccountDetailsByAccountId(accountId);
        verify(accountMapper, times(2)).entityToPayload(account);
    }
}