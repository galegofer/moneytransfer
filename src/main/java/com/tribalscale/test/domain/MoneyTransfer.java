package com.tribalscale.test.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MoneyTransfer {
    String currency;
    Double amount;
    String sourceAccount;
    String targetAccount;
}
