package com.tribalscale.test.mapper;

import com.tribalscale.test.domain.MoneyTransfer;
import com.tribalscale.test.domain.payload.MoneyTransferRequestPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MoneyTransferMapper {
    MoneyTransfer payloadToEntity(MoneyTransferRequestPayload payload);

    MoneyTransferRequestPayload entityToPayload(MoneyTransfer entity);
}
