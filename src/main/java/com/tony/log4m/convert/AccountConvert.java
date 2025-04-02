package com.tony.log4m.convert;

import com.tony.log4m.base.BasicConvert;
import com.tony.log4m.pojo.dto.AccountDTO;
import com.tony.log4m.pojo.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:04
 */
@Mapper
public interface AccountConvert extends BasicConvert<Account, AccountDTO> {
    AccountConvert INSTANCE = Mappers.getMapper(AccountConvert.class);
}
