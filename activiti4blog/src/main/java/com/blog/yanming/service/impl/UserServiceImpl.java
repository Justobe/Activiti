package com.blog.yanming.service.impl;

import com.blog.yanming.entity.User;
import com.blog.yanming.repository.UserRepository;
import com.blog.yanming.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by YanMing on 2017/7/13.
 */
@Service
@ComponentScan(basePackages = "com.blog.yanming.repository")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @Date: 16:56 2017/6/2
     * 根据id获取用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * @Date: 16:56 2017/6/2
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveNewUser(User user){
        String sql4user = "insert into t_user(username,password)VALUES (?,?)";
        //String sql4group = "insert into t_user(username,password)VALUES (?,?)"
        //KeyHolder resKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql4user, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1,user.getUsername());
                ps.setString(2,user.getPassword());
            }
        });
        //String uid = String.valueOf(resKeyHolder.getKey().intValue());

    }

}