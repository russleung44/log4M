package com.tony.log4m.convert;

import com.tony.log4m.annotation.IgnoreMetadata;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.models.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author Tony
 * @since 2022-09-23 15:13:05
 */
@Mapper
public interface CategoryConvert {
    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    @IgnoreMetadata
    void updateBill(@MappingTarget Bill bill, Category category);
}
