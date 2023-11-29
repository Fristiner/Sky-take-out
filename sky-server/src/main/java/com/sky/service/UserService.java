package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 10:20 11 29 10:20
 *
 */
public interface UserService {
    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
