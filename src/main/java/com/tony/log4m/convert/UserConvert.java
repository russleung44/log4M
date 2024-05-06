package com.tony.log4m.convert;

import com.tony.log4m.base.BasicConvert;
import com.tony.log4m.config.MapperSpringConfig;
import com.tony.log4m.pojo.dto.UserDTO;
import com.tony.log4m.pojo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:07
 */
@Mapper(config = MapperSpringConfig.class)
public interface UserConvert extends BasicConvert<User, UserDTO> {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);
}
