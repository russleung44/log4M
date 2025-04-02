package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.UserMapper;
import com.tony.log4m.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
