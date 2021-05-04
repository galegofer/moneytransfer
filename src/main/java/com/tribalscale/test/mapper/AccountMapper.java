package com.tribalscale.test.mapper;

import com.tribalscale.test.domain.Account;
import com.tribalscale.test.domain.payload.AccountPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account payloadToEntity(AccountPayload payload);

    AccountPayload entityToPayload(Account entity);
}
