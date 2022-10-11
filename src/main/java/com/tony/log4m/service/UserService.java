package com.tony.log4m.service;

import com.tony.log4m.base.CrudService;
import com.tony.log4m.pojo.dto.LoginDTO;
import com.tony.log4m.pojo.dto.UserDTO;
import com.tony.log4m.pojo.entity.User;

import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:25:10
 */
public interface UserService extends CrudService<User, UserDTO> {

    void register(User user);

    void login(LoginDTO loginDTO);

    Optional<User> getByTgUserId(Long tgUserId);
}