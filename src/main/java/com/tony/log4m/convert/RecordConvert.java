package com.tony.log4m.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:05
 */
@Mapper
public interface RecordConvert {
    RecordConvert INSTANCE = Mappers.getMapper(RecordConvert.class);
}
