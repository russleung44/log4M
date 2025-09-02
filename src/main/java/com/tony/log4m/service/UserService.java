package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.mapper.UserMapper;
import com.tony.log4m.pojo.entity.User;
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
public class UserService extends ServiceImpl<UserMapper, User> {

    public User getByTgUserId(Long tgUserId) {
        return this.lambdaQuery().eq(User::getTgUserId, tgUserId).one();
    }

    public void saveTgUser(Long tgUserId, String username) {
        User user = getByTgUserId(tgUserId);
        if (user == null) {
            new User().setTgUserId(tgUserId).setUsername(username).insert();
        }
    }

    // ========== 标准CRUD操作 ==========

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建的用户
     */
    public User create(User user) {
        // 检查用户名是否重复
        if (user.getUsername() != null && this.lambdaQuery().eq(User::getUsername, user.getUsername()).exists()) {
            throw new Log4mException("用户名重复");
        }
        // 检查TG用户ID是否重复
        if (user.getTgUserId() != null && this.lambdaQuery().eq(User::getTgUserId, user.getTgUserId()).exists()) {
            throw new Log4mException("Telegram用户已存在");
        }
        user.insert();
        return user;
    }

    /**
     * 根据ID更新用户（带数据验证）
     *
     * @param user 用户信息
     * @return 更新结果
     */
    public Boolean updateByIdWithValidation(User user) {
        // 检查用户名是否重复
        if (user.getUsername() != null && this.lambdaQuery().eq(User::getUsername, user.getUsername())
                .ne(User::getUserId, user.getUserId()).exists()) {
            throw new Log4mException("用户名重复");
        }
        // 检查TG用户ID是否重复
        if (user.getTgUserId() != null && this.lambdaQuery().eq(User::getTgUserId, user.getTgUserId())
                .ne(User::getUserId, user.getUserId()).exists()) {
            throw new Log4mException("Telegram用户已存在");
        }
        return user.updateById();
    }

    /**
     * 根据ID删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 删除结果
     */
    public Boolean deleteById(Serializable id) {
        User user = this.getOptById(id).orElseThrow(() -> new Log4mException("用户不存在"));
        return this.removeById(id);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public User getById(Serializable id) {
        return this.getOptById(id).orElseThrow(() -> new Log4mException("用户不存在"));
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<User> listAll() {
        return this.lambdaQuery().orderByDesc(User::getCrTime).list();
    }

    /**
     * 分页查询用户
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页结果
     */
    public Page<User> pageQuery(int current, int size) {
        Page<User> page = new Page<>(current, size);
        return this.lambdaQuery().orderByDesc(User::getCrTime).page(page);
    }
}
