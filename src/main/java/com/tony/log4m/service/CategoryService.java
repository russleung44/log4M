package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.convert.CategoryConvert;
import com.tony.log4m.dao.CategoryDao;
import com.tony.log4m.pojo.dto.CategoryDTO;
import com.tony.log4m.pojo.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryDao, Category> {

    public void insert(Category category) {
        // todo 检查账户名是否重复
        category.insert();
    }

    public void update(Category category) {
        Optional.ofNullable(this.getById(category.getId())).orElseThrow();
        // todo 检查账户名是否重复
        category.updateById();
    }

}
