package com.tribalscale.test.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Account {
    String accountId;
    String currency;
    Double balance;
}
