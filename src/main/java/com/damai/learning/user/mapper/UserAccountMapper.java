package com.damai.learning.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.user.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口。
 */
@Mapper
public interface UserAccountMapper
        extends BaseMapper<UserAccount> {
}