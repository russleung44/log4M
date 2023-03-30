package com.tony.log4m.service;


import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.TagConvert;
import com.tony.log4m.dao.TagDao;
import com.tony.log4m.pojo.dto.TagDTO;
import com.tony.log4m.pojo.entity.Tag;
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
public class TagService extends CrudServiceImpl<TagDao, Tag, TagDTO, TagConvert> {

    @Override
    public TagDTO insert(Tag tag) {
        super.checkName(tag.getName(), null);
        return super.insert(tag);
    }

    @Override
    public TagDTO update(Tag tag) {
        Optional.ofNullable(this.getById(tag.getId())).orElseThrow();
        super.checkName(tag.getName(), tag.getId());
        return super.update(tag);
    }

    @Override
    public TagDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    public void delete(Serializable id) {
        super.delete(id);
    }

}
