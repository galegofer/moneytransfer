package com.tribalscale.test.mapper

import com.tribalscale.test.domain.Account
import com.tribalscale.test.domain.payload.AccountPayload
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface AccountMapper {
    fun payloadToEntity(payload: AccountPayload): Account
    fun entityToPayload(entity: Account): AccountPayload
}