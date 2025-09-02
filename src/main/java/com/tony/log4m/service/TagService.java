package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.bots.handlers.CallbackProcessor;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.TagMapper;
import com.tony.log4m.models.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;


/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class TagService extends ServiceImpl<TagMapper, Tag> {


    public String getTagName(Long tagId, CallbackProcessor callbackProcessor) {
        return getOptById(tagId)
                .map(Tag::getTagName)
                .orElse("无标签");
    }

    // ========== 标准CRUD操作 ==========

    /**
     * 创建标签
     *
     * @param tag 标签信息
     * @return 创建的标签
     */
    public Tag create(Tag tag) {
        // 检查标签名是否重复
        if (this.lambdaQuery().eq(Tag::getTagName, tag.getTagName()).exists()) {
            throw new Log4mException("标签名称重复");
        }
        tag.insert();
        return tag;
    }

    /**
     * 根据ID更新标签（带数据验证）
     *
     * @param tag 标签信息
     * @return 更新结果
     */
    public Boolean updateByIdWithValidation(Tag tag) {
        // 检查标签名是否重复
        if (this.lambdaQuery().eq(Tag::getTagName, tag.getTagName())
                .ne(Tag::getTagId, tag.getTagId()).exists()) {
            throw new Log4mException("标签名称重复");
        }
        return tag.updateById();
    }

    /**
     * 根据ID删除标签（逻辑删除）
     *
     * @param id 标签ID
     * @return 删除结果
     */
    public Boolean deleteById(Serializable id) {
        Tag tag = this.getOptById(id).orElseThrow(() -> new Log4mException("标签不存在"));
        return this.removeById(id);
    }

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签信息
     */
    public Tag getById(Serializable id) {
        return this.getOptById(id).orElseThrow(() -> new Log4mException("标签不存在"));
    }

    /**
     * 查询所有标签
     *
     * @return 标签列表
     */
    public List<Tag> listAll() {
        return this.lambdaQuery().orderByDesc(Tag::getCrTime).list();
    }

    /**
     * 分页查询标签
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页结果
     */
    public Page<Tag> pageQuery(int current, int size) {
        Page<Tag> page = new Page<>(current, size);
        return this.lambdaQuery().orderByDesc(Tag::getCrTime).page(page);
    }

    /**
     * 按标签名称搜索
     *
     * @param tagName 标签名称
     * @return 标签列表
     */
    public List<Tag> searchByName(String tagName) {
        return this.lambdaQuery().like(Tag::getTagName, tagName)
                .orderByDesc(Tag::getCrTime).list();
    }

    /**
     * 获取或创建标签
     *
     * @param tagName 标签名称
     * @return 标签信息
     */
    public Tag getOrCreate(String tagName) {
        return this.lambdaQuery().eq(Tag::getTagName, tagName)
                .oneOpt().orElseGet(() -> {
                    Tag tag = new Tag().setTagName(tagName);
                    tag.insert();
                    return tag;
                });
    }
}
