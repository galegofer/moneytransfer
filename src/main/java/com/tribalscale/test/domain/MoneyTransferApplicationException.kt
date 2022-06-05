package com.tribalscale.test.domain

class MoneyTransferApplicationException(message: String, val statusCode: Int) :
    RuntimeException(message)