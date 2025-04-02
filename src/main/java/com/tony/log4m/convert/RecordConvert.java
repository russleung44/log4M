package com.tony.log4m.convert;

import com.tony.log4m.base.BasicConvert;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:05
 */
@Mapper
public interface RecordConvert extends BasicConvert<Bill, RecordDTO> {
    RecordConvert INSTANCE = Mappers.getMapper(RecordConvert.class);
}
