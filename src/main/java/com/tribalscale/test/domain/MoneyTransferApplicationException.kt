package com.tribalscale.test.domain

import lombok.ToString

@ToString
class MoneyTransferApplicationException(message: String, val statusCode: Int) :
    RuntimeException(message)