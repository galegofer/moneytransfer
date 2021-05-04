package com.tribalscale.test.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MoneyTransferApplicationException extends RuntimeException {

    private final int statusCode;

    @Builder
    public MoneyTransferApplicationException(final String message, final int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
