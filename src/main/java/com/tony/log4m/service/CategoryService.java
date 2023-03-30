package com.tony.log4m.service;


import com.tony.log4m.base.CrudServiceImpl;
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
public class CategoryService extends CrudServiceImpl<CategoryDao, Category, CategoryDTO, CategoryConvert> {

    @Override
    public CategoryDTO insert(Category category) {
        super.checkName(category.getName(), null);
        return super.insert(category);
    }

    @Override
    public CategoryDTO update(Category category) {
        Optional.ofNullable(this.getById(category.getId())).orElseThrow();
        super.checkName(category.getName(), category.getId());
        return super.update(category);
    }

    @Override
    public CategoryDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    public void delete(Serializable id) {
        super.delete(id);
    }

}
