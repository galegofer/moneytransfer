package com.tribalscale.test.domain.payload

import lombok.extern.jackson.Jacksonized

@Jacksonized
class ErrorPayload(
    val code: Int,
    val message: String
)