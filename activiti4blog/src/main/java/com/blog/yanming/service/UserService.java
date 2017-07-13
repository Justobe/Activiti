package com.blog.yanming.service;

import com.blog.yanming.entity.User;

/**
 * Created by YanMing on 2017/7/13.
 */
public interface UserService {

    User getUserById(Long id);
    User getUserByUsername(String username);
    void saveNewUser(User user);
}
