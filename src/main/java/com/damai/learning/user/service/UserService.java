package com.damai.learning.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.common.security.JwtTokenUtil;
import com.damai.learning.user.dto.UserLoginRequest;
import com.damai.learning.user.dto.UserRegisterRequest;
import com.damai.learning.user.entity.UserAccount;
import com.damai.learning.user.mapper.UserAccountMapper;
import com.damai.learning.user.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;

/**
 * 用户业务服务。
 */
@Service
public class UserService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-minutes}")
    private long jwtExpirationMinutes;

    /**
     * 注册新用户。
     *
     * @return 新用户id
     */
    @Transactional
    public Long register(UserRegisterRequest request) {
        LambdaQueryWrapper<UserAccount> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper.eq(
                UserAccount::getUsername,
                request.getUsername()
        );

        Long existingCount =
                userAccountMapper.selectCount(queryWrapper);

        if (existingCount > 0) {
            throw new BusinessException(
                    CommonErrorCode.USERNAME_ALREADY_EXISTS
            );
        }

        UserAccount userAccount = new UserAccount();

        userAccount.setUsername(request.getUsername());
        userAccount.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );
        userAccount.setNickname(request.getNickname());
        userAccount.setUserStatus(1);
        userAccount.setStatus(1);

        try {
            userAccountMapper.insert(userAccount);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(
                    CommonErrorCode.USERNAME_ALREADY_EXISTS
            );
        }

        return userAccount.getId();
    }

    /**
     * 校验用户名和密码并签发JWT。
     */
    public UserLoginVO login(UserLoginRequest request) {
        LambdaQueryWrapper<UserAccount> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper
                .eq(
                        UserAccount::getUsername,
                        request.getUsername()
                )
                .eq(UserAccount::getStatus, 1);

        UserAccount userAccount =
                userAccountMapper.selectOne(queryWrapper);

        if (userAccount == null
                || !passwordEncoder.matches(
                request.getPassword(),
                userAccount.getPassword()
        )) {
            throw new BusinessException(
                    CommonErrorCode.USERNAME_OR_PASSWORD_ERROR
            );
        }

        if (!Objects.equals(
                userAccount.getUserStatus(),
                1
        )) {
            throw new BusinessException(
                    CommonErrorCode.USER_DISABLED
            );
        }

        String accessToken = JwtTokenUtil.createToken(
                userAccount.getId(),
                userAccount.getUsername(),
                jwtSecret,
                jwtExpirationMinutes
        );

        long expirationSeconds = Duration.ofMinutes(
                jwtExpirationMinutes
        ).toSeconds();

        return new UserLoginVO(
                accessToken,
                "Bearer",
                expirationSeconds
        );
    }
}