package com.tony.log4m.convert;

import com.tony.log4m.base.BasicConvert;
import com.tony.log4m.pojo.dto.CategoryDTO;
import com.tony.log4m.pojo.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:05
 */
@Mapper
public interface CategoryConvert extends BasicConvert<Category, CategoryDTO> {
    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);
}
