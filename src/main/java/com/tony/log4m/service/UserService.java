package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.dao.UserDao;
import com.tony.log4m.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserDao, User> {

    public Optional<User> getByTgUserId(Long tgUserId) {
        return this.query().eq("tg_user_id", tgUserId).oneOpt();
    }

}
