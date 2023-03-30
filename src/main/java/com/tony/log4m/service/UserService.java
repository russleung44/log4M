package com.tony.log4m.service;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.UserConvert;
import com.tony.log4m.dao.UserDao;
import com.tony.log4m.pojo.dto.LoginDTO;
import com.tony.log4m.pojo.dto.UserDTO;
import com.tony.log4m.pojo.entity.User;
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
public class UserService extends CrudServiceImpl<UserDao, User, UserDTO, UserConvert> {

    @Override
    public UserDTO update(User user) {
        Optional.ofNullable(this.getById(user.getId())).orElseThrow();
        return super.update(user);
    }

    @Override
    public UserDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    public void delete(Serializable id) {
        super.delete(id);
    }

    public void register(User user) {
        super.insert(user);
    }

    public SaTokenInfo login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user = this.lambdaQuery().eq(User::getUsername, username).oneOpt().orElseThrow(() -> new RuntimeException("用户名不存在"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        StpUtil.login(user.getId());

        return StpUtil.getTokenInfo();
    }

    public Optional<User> getByTgUserId(Long tgUserId) {
        return this.query().eq("tg_user_id", tgUserId).oneOpt();
    }

}
