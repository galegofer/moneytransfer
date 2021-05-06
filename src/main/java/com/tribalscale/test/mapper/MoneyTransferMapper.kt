package com.tribalscale.test.mapper

import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload
import com.tribalscale.test.domain.MoneyTransfer
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MoneyTransferMapper {
    fun payloadToEntity(payload: MoneyTransferRequestPayload): MoneyTransfer
    fun entityToPayload(entity: MoneyTransfer): MoneyTransferRequestPayload
}