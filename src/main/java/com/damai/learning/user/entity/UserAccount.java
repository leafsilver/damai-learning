package com.damai.learning.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应数据库表d_user。
 */
@Data
@TableName("d_user")
public class UserAccount {

    /** 用户主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名。 */
    private String username;

    /** 密码哈希，不保存明文密码。 */
    private String password;

    /** 用户昵称。 */
    private String nickname;

    /** 用户状态：1正常，0禁用。 */
    private Integer userStatus;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 修改时间。 */
    private LocalDateTime editTime;

    /** 数据状态：1正常，0删除。 */
    private Integer status;
}