package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.dao.TagDao;
import com.tony.log4m.pojo.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class TagService extends ServiceImpl<TagDao, Tag> {


}
