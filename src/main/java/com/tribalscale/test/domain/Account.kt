package com.tribalscale.test.domain

import lombok.With

@With
class Account(
    val accountId: String,
    val currency: String,
    val balance: Double
)