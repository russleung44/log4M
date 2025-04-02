package com.tony.log4m.convert;

import com.tony.log4m.base.BasicConvert;
import com.tony.log4m.pojo.dto.TagDTO;
import com.tony.log4m.pojo.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:06
 */
@Mapper
public interface TagConvert extends BasicConvert<Tag, TagDTO> {
    TagConvert INSTANCE = Mappers.getMapper(TagConvert.class);
}
