package com.tony.log4m.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<User> {

    @TableId
    private Long userId;

    private Long tgUserId;

    private String username;

    private String password;

    private String email;

    private Integer status;

    private Integer defaultAccountId;

}
