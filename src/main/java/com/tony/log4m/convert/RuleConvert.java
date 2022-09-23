package com.tony.log4m.convert;

import com.tony.log4m.base.BasicConvert;
import com.tony.log4m.config.MapperSpringConfig;
import com.tony.log4m.pojo.dto.RuleDTO;
import com.tony.log4m.pojo.entity.Rule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:06
 */
@Mapper(config = MapperSpringConfig.class)
public interface RuleConvert extends BasicConvert<Rule, RuleDTO> {
    RuleConvert INSTANCE = Mappers.getMapper(RuleConvert.class);
}
